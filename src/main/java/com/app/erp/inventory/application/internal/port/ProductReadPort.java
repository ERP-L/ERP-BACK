package com.app.erp.inventory.application.internal.port;

import com.app.erp.inventory.application.internal.messages.results.ProductResult;

import java.util.List;

public interface ProductReadPort {
    List<ProductResult> getAllByCompany(int companyId);
}
