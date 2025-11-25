package com.app.erp.inventoryrefactor.product.application.service;

import com.app.erp.inventoryrefactor.product.domain.Product;
import com.app.erp.inventoryrefactor.product.infrastructure.repository.ProductRepositoryPort;
import com.app.erp.inventoryrefactor.product.interfaces.rest.ProductResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductServicePort {

    private final ProductRepositoryPort repo;

    public ProductServiceImpl(ProductRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public ProductResponse getProductById(int companyId, int productId) {
        Product p = repo.findById(productId);
        if (p == null) return null;
        if (p.companyId() != companyId) {
            throw new AccessDeniedException("company mismatch");
        }
        return new ProductResponse(
                p.productId(),
                p.sku(),
                p.productName(),
                p.categoryId(),
                p.uomId(),
                p.isSerialized(),
                p.isBatchControlled(),
                p.reorderLevel(),
                p.leadTimeDays(),
                p.weight(),
                p.volume(),
                p.status(),
                p.createdUtc(),
                p.updatedUtc(),
                p.companyId()
        );
    }
}
