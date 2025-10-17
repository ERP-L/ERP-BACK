package com.app.erp.inventory.application.internal.port;

import com.app.erp.inventory.application.internal.messages.results.ReparentProductCategoryResult;
import java.util.List;

public interface ProductCategoryReadPort {
    java.util.List<ReparentProductCategoryResult> getAllByCompany(int companyId, boolean onlyActive);
}
