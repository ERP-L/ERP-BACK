package com.app.erp.inventory.application.port;

import com.app.erp.inventory.application.dtos.results.ProductResult;

import java.util.List;

public interface ProductReadPort {
    List<ProductResult> getAllByCompany(int companyId);
}
