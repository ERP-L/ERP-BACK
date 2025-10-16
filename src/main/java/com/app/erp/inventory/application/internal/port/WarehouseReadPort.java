package com.app.erp.inventory.application.internal.port;

import com.app.erp.inventory.application.internal.messages.results.CreateWarehouseResult;
import java.util.List;

/** Lecturas para warehouses. */
public interface WarehouseReadPort {
    java.util.List<CreateWarehouseResult> listWarehousesByCompany(int companyId, Boolean onlyActive, Integer branchId);
}
