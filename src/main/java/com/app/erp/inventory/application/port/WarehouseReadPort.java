package com.app.erp.inventory.application.port;

import com.app.erp.inventory.application.dtos.results.CreateWarehouseResult;
import java.util.List;

/** Lecturas para warehouses. */
public interface WarehouseReadPort {
    java.util.List<CreateWarehouseResult> listWarehousesByCompany(int companyId, Boolean onlyActive, Integer branchId);
}
