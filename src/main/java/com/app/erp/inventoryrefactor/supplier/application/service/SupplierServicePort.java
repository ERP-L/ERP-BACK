package com.app.erp.inventoryrefactor.supplier.application.service;

import com.app.erp.inventoryrefactor.common.PageResult;
import com.app.erp.inventoryrefactor.supplier.domain.ProductSummary;
import com.app.erp.inventoryrefactor.supplier.domain.Supplier;

import java.util.List;

public interface SupplierServicePort {
    int create(int companyId, Supplier supplier);

    PageResult<Supplier> getAll(int companyId, String search, Boolean active, String productIds, int page, int pageSize);

    List<ProductSummary> getProducts(int companyId, int supplierId);

    void addProducts(int companyId, int supplierId, java.util.List<Integer> productIds);
}
