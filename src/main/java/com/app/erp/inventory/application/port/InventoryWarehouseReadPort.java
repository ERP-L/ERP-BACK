package com.app.erp.inventory.application.port;

import com.app.erp.inventory.application.dtos.results.InventoryProductResult;

import java.util.List;

public interface InventoryWarehouseReadPort {
    List<com.app.erp.inventory.application.dtos.results.InventoryProductResult> listProductsInWarehouse(
            int companyId,
            int warehouseId,
            Integer productId,
            Integer categoryId,
            String search,
            String orderBy,
            int pageNumber,
            int pageSize
    );
}
