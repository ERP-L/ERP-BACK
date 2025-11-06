package com.app.erp.inventory.application.usecase;

import com.app.erp.inventory.application.dtos.results.LocationResult;
import com.app.erp.inventory.application.port.LocationReadPort;
import com.app.erp.shared.security.AuthContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListLocationsHandler {

    private final LocationReadPort readPort;

    public ListLocationsHandler(LocationReadPort readPort) {
        this.readPort = readPort;
    }

    public List<LocationResult> handle(AuthContext auth, int warehouseId, Boolean onlyAllowStock) {
        if (auth == null || auth.companyId() == null) throw new com.app.erp.shared.exceptions.AuthorizationException("Token sin companyId");
        return readPort.listLocations(warehouseId, onlyAllowStock);
    }
}
