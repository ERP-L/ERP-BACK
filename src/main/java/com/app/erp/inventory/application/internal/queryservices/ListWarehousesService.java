package com.app.erp.inventory.application.internal.queryservices;

import com.app.erp.inventory.application.internal.messages.results.CreateWarehouseResult;
import com.app.erp.inventory.application.internal.port.WarehouseReadPort;
import com.app.erp.shared.exceptions.AuthorizationException;
import com.app.erp.shared.security.AuthContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ListWarehousesService {

    private final WarehouseReadPort readPort;

    public ListWarehousesService(WarehouseReadPort readPort) {
        this.readPort = Objects.requireNonNull(readPort);
    }

    public List<CreateWarehouseResult> handle(AuthContext auth, Boolean onlyActive, Integer branchId) {
        if (auth == null) throw new AuthorizationException("No autorizado (sin contexto)");
        if (auth.companyId() == null) throw new AuthorizationException("Token sin companyId (cid)");

        return readPort.listWarehousesByCompany(auth.companyId(), onlyActive, branchId);
    }
}
