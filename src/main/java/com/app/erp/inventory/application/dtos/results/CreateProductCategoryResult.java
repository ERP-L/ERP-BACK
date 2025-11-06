package com.app.erp.inventory.application.dtos.results;

public class CreateProductCategoryResult {
    private Integer categoryId;

    public CreateProductCategoryResult() {}
    public CreateProductCategoryResult(Integer categoryId) { this.categoryId = categoryId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
}
