package com.app.erp.inventoryrefactor.product.application.service;

import com.app.erp.inventoryrefactor.product.interfaces.rest.ProductResponse;

public interface ProductServicePort {
    ProductResponse getProductById(int companyId, int productId);
}
