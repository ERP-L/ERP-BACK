package com.app.erp.inventory.application.usecase;

import com.app.erp.inventory.application.dtos.commands.CreateLocationCommand;
import com.app.erp.inventory.application.dtos.results.CreateLocationResult;
import com.app.erp.inventory.application.port.LocationWritePort;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.exceptions.InvalidInputException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CreateLocationHandler {

    private final LocationWritePort writePort;

    public CreateLocationHandler(LocationWritePort writePort) {
        this.writePort = Objects.requireNonNull(writePort);
    }

    @Transactional
    public CreateLocationResult handle(CreateLocationCommand cmd, AuthContext auth) {
        if (auth == null || auth.companyId() == null) throw new com.app.erp.shared.exceptions.AuthorizationException("Token sin companyId");

        if (cmd.getWarehouseId() == null) throw new InvalidInputException("warehouseId es requerido.");
        if (cmd.getCode() == null || cmd.getCode().trim().isEmpty()) throw new InvalidInputException("code es requerido.");

        if (cmd.getAllowStock() == null) cmd.setAllowStock(Boolean.TRUE);

        return writePort.createLocation(cmd);
    }
}
