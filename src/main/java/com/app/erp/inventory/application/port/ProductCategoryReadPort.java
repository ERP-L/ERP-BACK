package com.app.erp.inventory.application.port;

import com.app.erp.inventory.application.dtos.results.ReparentProductCategoryResult;
import java.util.List;

public interface ProductCategoryReadPort {
    java.util.List<ReparentProductCategoryResult> getAllByCompany(int companyId, boolean onlyActive);
}
