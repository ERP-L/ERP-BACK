package com.app.erp.inventory.application.internal.messages.results;

public class CreateProductCategoryResult {
    private Integer categoryId;

    public CreateProductCategoryResult() {}
    public CreateProductCategoryResult(Integer categoryId) { this.categoryId = categoryId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
}
