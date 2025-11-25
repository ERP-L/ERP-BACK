package com.app.erp.inventoryrefactor.supplier.application.service;

import com.app.erp.inventoryrefactor.common.PageResult;
import com.app.erp.inventoryrefactor.supplier.domain.ProductSummary;
import com.app.erp.inventoryrefactor.supplier.domain.Supplier;
import com.app.erp.inventoryrefactor.supplier.infrastructure.repository.SupplierRepositoryPort;
import com.app.erp.shared.security.AuthContextResolver;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl implements SupplierServicePort {

    private final SupplierRepositoryPort repository;
    private final AuthContextResolver authResolver;

    public SupplierServiceImpl(SupplierRepositoryPort repository, AuthContextResolver authResolver) {
        this.repository = repository;
        this.authResolver = authResolver;
    }

    @Override
    public int create(int companyId, Supplier supplier) {
        if (supplier.companyId() != companyId) throw new SecurityException("Invalid tenant");
        return repository.create(supplier);
    }

    @Override
    public PageResult<Supplier> getAll(int companyId, String search, Boolean active, String productIds, int page, int pageSize) {
        return repository.findAll(companyId, search, active, productIds, page, pageSize);
    }

    @Override
    public List<ProductSummary> getProducts(int companyId, int supplierId) {
        // No company validation here because usp returns only product list by supplier id.
        return repository.findProductsBySupplier(supplierId);
    }

    @Override
    public void addProducts(int companyId, int supplierId, java.util.List<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) return;
        // Loop and call repo for each product; if any call fails, DbErrorTranslator will throw a translated runtime exception
        for (Integer pid : productIds) {
            repository.addProductToSupplier(supplierId, pid);
        }
    }
}
