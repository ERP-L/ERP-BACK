package com.app.erp.inventory.application.usecase;

import com.app.erp.inventory.application.dtos.commands.PostInventoryMovementCommand;
import com.app.erp.inventory.application.dtos.results.PostInventoryMovementResult;
import com.app.erp.inventory.application.port.InventoryWritePort;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.inventory.application.dtos.commands.InventoryLineCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class PostInventoryMovementHandler {

    private final InventoryWritePort writePort;

    @Autowired
    public PostInventoryMovementHandler(InventoryWritePort writePort) {
        this.writePort = writePort;
    }

    public PostInventoryMovementResult handle(PostInventoryMovementCommand command, AuthContext authContext) {
        // Minimal syntactic validations (shape-only)
        if (command == null) throw new IllegalArgumentException("command required");
        if (command.getMovementType() == null || command.getMovementType().isBlank())
            throw new IllegalArgumentException("movementType required (IN|OUT|TRF|ADJ)");
        if (command.getLineMode() == null || command.getLineMode().isBlank())
            throw new IllegalArgumentException("lineMode required (NORMAL|BATCH|SERIAL)");
        if (command.getLines() == null || command.getLines().isEmpty())
            throw new IllegalArgumentException("lines required");

        String movement = command.getMovementType().toUpperCase();
        String lineMode = command.getLineMode().toUpperCase();

        // Header shape checks per movement type
        switch (movement) {
            case "IN":
            case "OPENING":
                if (command.getToWarehouseId() == null)
                    throw new IllegalArgumentException("toWarehouseId required for IN/OPENING");
                break;
            case "OUT":
                if (command.getFromWarehouseId() == null)
                    throw new IllegalArgumentException("fromWarehouseId required for OUT");
                break;
            case "TRF":
                if (command.getFromWarehouseId() == null || command.getToWarehouseId() == null)
                    throw new IllegalArgumentException("fromWarehouseId and toWarehouseId required for TRF");
                break;
            case "ADJ":
                if (command.getFromWarehouseId() == null && command.getToWarehouseId() == null)
                    throw new IllegalArgumentException("warehouseId required for ADJ (from or to)");
                break;
            default:
                throw new IllegalArgumentException("unsupported movementType: " + movement);
        }

        // Enforce declared lineMode
        boolean anySerial = false;
        boolean anyNonSerial = false;
        List<InventoryLineCommand> lines = command.getLines();
        for (InventoryLineCommand l : lines) {
            boolean serialPresent = (l.getSerialId() != null) || (l.getSerialNumber() != null && !l.getSerialNumber().trim().isEmpty());
            if (serialPresent) anySerial = true; else anyNonSerial = true;
        }

        if ("SERIAL".equals(lineMode)) {
            if (anyNonSerial)
                throw new IllegalArgumentException("lineMode=SERIAL but non-serial lines present");
            // duplicate serials and quantity rules
            HashSet<Object> seen = new HashSet<>();
            for (InventoryLineCommand l : lines) {
                Object key = l.getSerialId() != null ? l.getSerialId() : l.getSerialNumber();
                if (key == null)
                    throw new IllegalArgumentException("serialId or serialNumber required for lineMode=SERIAL");
                if (!seen.add(key))
                    throw new IllegalArgumentException("duplicated serial in payload");

                if (l.getQuantity() == null)
                    throw new IllegalArgumentException("quantity required for serial lines");
                int q = l.getQuantity().intValue();
                if ("ADJ".equals(movement)) {
                    if (q != 1 && q != -1)
                        throw new IllegalArgumentException("serial lines: quantity must be 1 (or -1 in ADJ)");
                } else if (q != 1) {
                    throw new IllegalArgumentException("serial lines: quantity must be 1");
                }
            }
        } else { // NON-SERIAL declared mode
            if (anySerial)
                throw new IllegalArgumentException("lineMode!=SERIAL but serial lines present");
        }

        // Delegate to repository
        return writePort.postMovement(command, authContext);
    }
}
