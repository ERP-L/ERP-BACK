package com.app.erp.inventory.application.port;

import com.app.erp.inventory.application.dtos.results.LocationResult;
import java.util.List;

public interface LocationReadPort {
    java.util.List<LocationResult> listLocations(int warehouseId, Boolean onlyAllowStock);
}
