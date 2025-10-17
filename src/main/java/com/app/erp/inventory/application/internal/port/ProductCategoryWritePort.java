package com.app.erp.inventory.application.internal.port;

public interface ProductCategoryWritePort {
    Integer createProductCategory(int companyId, String categoryName, String description, Integer parentCategoryId, boolean isActive);
    com.app.erp.inventory.application.internal.messages.results.ReparentProductCategoryResult reparentProductCategory(int companyId, int categoryId, Integer newParentCategoryId);
}
