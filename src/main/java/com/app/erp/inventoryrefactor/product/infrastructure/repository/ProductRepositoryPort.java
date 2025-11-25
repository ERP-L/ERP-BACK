package com.app.erp.inventoryrefactor.product.infrastructure.repository;

import com.app.erp.inventoryrefactor.product.domain.Product;

public interface ProductRepositoryPort {
    Product findById(int productId);
}
