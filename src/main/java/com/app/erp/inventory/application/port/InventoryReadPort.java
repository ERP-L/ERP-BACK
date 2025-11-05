package com.app.erp.inventory.application.port;

import com.app.erp.inventory.application.dtos.results.ProductDetailsResult;
import com.app.erp.shared.security.AuthContext;

/** Lecturas del BC Inventory para validaciones previas. */
public interface InventoryReadPort {
    ProductDetailsResult getProductDetailsInWarehouse(int warehouseId, int productId,
                                                      String orderBatch, int pageBatch, int sizeBatch,
                                                      String orderSerial, int pageSerial, int sizeSerial,
                                                      AuthContext auth);

    /**
     * @return CompanyID dueño de la categoría, o null si CategoryID no existe.
     */
    Integer getCategoryCompanyId(int categoryId);
}
