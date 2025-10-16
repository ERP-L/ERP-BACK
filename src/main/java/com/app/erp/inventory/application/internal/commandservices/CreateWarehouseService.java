package com.app.erp.inventory.application.internal.commandservices;

import com.app.erp.inventory.application.internal.messages.commands.CreateWarehouseCommand;
import com.app.erp.inventory.application.internal.messages.results.CreateWarehouseResult;
import com.app.erp.inventory.application.internal.port.WarehouseWritePort;
import com.app.erp.inventory.application.internal.security.InventoryAuthorizationPolicy;
import com.app.erp.shared.exceptions.InvalidInputException;
import com.app.erp.shared.security.AuthContext;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CreateWarehouseService {

    private final InventoryAuthorizationPolicy policy;
    private final WarehouseWritePort writePort;

    public CreateWarehouseService(InventoryAuthorizationPolicy policy, WarehouseWritePort writePort) {
        this.policy = policy;
        this.writePort = writePort;
    }

    public CreateWarehouseResult handle(CreateWarehouseCommand cmd, AuthContext auth) {
        // Validaciones de forma (rápidas, previas al SP)
        if (cmd.getBranchId() == null) {
            throw new InvalidInputException("branchId es requerido.");
        }
        if (cmd.getWarehouseName() == null || cmd.getWarehouseName().trim().isEmpty()) {
            throw new InvalidInputException("warehouseName es requerido.");
        }

        cmd.setIsActive(Boolean.TRUE);
        cmd.setCreatedUtc(Instant.now());

        // Autorización (RBAC + ownership)
        policy.checkCreateWarehouse(cmd, auth);

        // Persistencia vía SP
        return writePort.createWarehouse(cmd);
    }
}
