package com.app.erp.inventory.application.usecase;

import com.app.erp.inventory.application.dtos.commands.PostInventoryMovementCommand;
import com.app.erp.inventory.application.dtos.results.PostInventoryMovementResult;
import com.app.erp.inventory.application.port.InventoryWritePort;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.inventory.application.dtos.commands.InventoryLineCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        // Special rule: TRF + SERIAL or TRF + BATCH requires an explicit locationCode per line so
        // a new ItemLocation can be created in the destination warehouse.
        if ("TRF".equals(movement) && ("SERIAL".equals(lineMode) || "BATCH".equals(lineMode))) {
            // Ensure the repository will attempt to create locations
            if (!command.isAutoCreateLocation()) command.setAutoCreateLocation(true);
            for (InventoryLineCommand l : command.getLines()) {
                if (l.getLocationCode() == null || l.getLocationCode().trim().isEmpty()) {
                    throw new IllegalArgumentException("locationCode required for TRF when lineMode=SERIAL or BATCH (one locationCode per line is required)");
                }
            }
        }

        // New rule: NORMAL mode requires explicit locationCode for IN and TRF (we always need to know where
        // the items are stored). Force autoCreateLocation so repository will create ItemLocation entries.
        if ("NORMAL".equals(lineMode) && ("IN".equals(movement) || "TRF".equals(movement))) {
            if (!command.isAutoCreateLocation()) command.setAutoCreateLocation(true);
            for (InventoryLineCommand l : command.getLines()) {
                if (l.getLocationCode() == null || l.getLocationCode().trim().isEmpty()) {
                    throw new IllegalArgumentException("locationCode required for IN/TRF when lineMode=NORMAL (one locationCode per line is required)");
                }
            }
        }

        // Force autoCreateLocation when auto-creating batch/serial, and ensure locationCode is not blank when required
        boolean needsLocation = command.isAutoCreateLocation() || command.isAutoCreateBatch() || command.isAutoCreateSerial();
        if (needsLocation && !command.isAutoCreateLocation()) {
            command.setAutoCreateLocation(true);
        }
        if (command.isAutoCreateLocation()) {
            for (InventoryLineCommand l : command.getLines()) {
                // For TRF+SERIAL or TRF+BATCH we already validated presence of locationCode above; keep the provided code
                if ("TRF".equals(movement) && ("SERIAL".equals(lineMode) || "BATCH".equals(lineMode))) continue;
                if (l.getLocationCode() == null || l.getLocationCode().trim().isEmpty()) {
                    l.setLocationCode(generateDefaultLocationCode(command.getToWarehouseId(), movement));
                }
            }
        }

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

        // New: validations for BATCH mode on OUT/TRF/ADJ
        if ("BATCH".equals(lineMode)) {
            for (InventoryLineCommand l : lines) {
                boolean hasBatchId = l.getBatchId() != null;
                boolean hasBatchNumber = l.getBatchNumber() != null && !l.getBatchNumber().trim().isEmpty();
                // For movements where we are removing/moving stock, prefer an existing BatchID.
                if ("OUT".equals(movement) || "TRF".equals(movement) || "ADJ".equals(movement)) {
                    if (!hasBatchId) {
                        // allow when client explicitly requests auto-creation and provides a batchNumber
                        if (!(command.isAutoCreateBatch() && hasBatchNumber)) {
                            throw new IllegalArgumentException("batchId required for movementType OUT/TRF/ADJ when lineMode=BATCH (or provide batchNumber with autoCreateBatch=true)");
                        }
                    }
                }
            }
        }

        // Delegate to repository
        return writePort.postMovement(command, authContext);
    }

    private String generateDefaultLocationCode(Integer toWarehouseId, String movement) {
        // Deterministic, human-readable fallback location code
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String wh = toWarehouseId == null ? "X" : toWarehouseId.toString();
        String mv = (movement == null || movement.isBlank()) ? "MV" : movement.toUpperCase();
        return "AUTO-" + mv + "-" + wh + "-" + ts;
    }
}
