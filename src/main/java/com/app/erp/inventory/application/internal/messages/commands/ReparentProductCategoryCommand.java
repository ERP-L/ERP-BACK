package com.app.erp.inventory.application.internal.messages.commands;

public class ReparentProductCategoryCommand {
    private int categoryId;
    private Integer newParentCategoryId;

    public ReparentProductCategoryCommand() {}
    public ReparentProductCategoryCommand(int categoryId, Integer newParentCategoryId) {
        this.categoryId = categoryId;
        this.newParentCategoryId = newParentCategoryId;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public Integer getNewParentCategoryId() { return newParentCategoryId; }
    public void setNewParentCategoryId(Integer newParentCategoryId) { this.newParentCategoryId = newParentCategoryId; }
}
