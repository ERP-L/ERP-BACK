package com.app.erp.inventory.application.usecase;

import com.app.erp.inventory.application.dtos.results.CreateWarehouseResult;
import com.app.erp.inventory.application.port.WarehouseReadPort;
import com.app.erp.shared.exceptions.AuthorizationException;
import com.app.erp.shared.security.AuthContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ListWarehousesHandler {

    private final WarehouseReadPort readPort;

    public ListWarehousesHandler(WarehouseReadPort readPort) {
        this.readPort = Objects.requireNonNull(readPort);
    }

    public List<CreateWarehouseResult> handle(AuthContext auth, Boolean onlyActive, Integer branchId) {
        if (auth == null) throw new AuthorizationException("No autorizado (sin contexto)");
        if (auth.companyId() == null) throw new AuthorizationException("Token sin companyId (cid)");

        return readPort.listWarehousesByCompany(auth.companyId(), onlyActive, branchId);
    }
}
