package com.app.erp.inventory.interfaces.rest.contracts;

public class ProductCategoryResponse {
    private Integer categoryId;

    public ProductCategoryResponse() {}
    public ProductCategoryResponse(Integer categoryId) { this.categoryId = categoryId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
}
