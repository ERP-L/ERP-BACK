package com.app.erp.inventory.interfaces.rest.contracts;

public class ReparentProductCategoryRequest {
    private Integer newParentCategoryId;

    public Integer getNewParentCategoryId() { return newParentCategoryId; }
    public void setNewParentCategoryId(Integer newParentCategoryId) { this.newParentCategoryId = newParentCategoryId; }
}
