package com.app.erp.inventoryrefactor.supplier.infrastructure.repository;

import com.app.erp.inventoryrefactor.common.PageResult;
import com.app.erp.inventoryrefactor.supplier.domain.ProductSummary;
import com.app.erp.inventoryrefactor.supplier.domain.Supplier;

import java.util.List;

public interface SupplierRepositoryPort {
    int create(Supplier supplier);

    PageResult<Supplier> findAll(int companyId, String search, Boolean active, String productIds, int page, int pageSize);

    List<ProductSummary> findProductsBySupplier(int supplierId);

    void addProductToSupplier(int supplierId, int productId);
}
