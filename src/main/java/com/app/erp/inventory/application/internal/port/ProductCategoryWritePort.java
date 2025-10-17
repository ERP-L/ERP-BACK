package com.app.erp.inventory.application.internal.port;

public interface ProductCategoryWritePort {
    Integer createProductCategory(int companyId, String categoryName, String description, Integer parentCategoryId, boolean isActive);
}
