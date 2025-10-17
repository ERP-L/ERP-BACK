package com.app.erp.inventory.application.internal.messages.results;

import java.time.OffsetDateTime;

public class ReparentProductCategoryResult {
    private int categoryId;
    private String categoryName;
    private String description;
    private Integer parentCategoryId;
    private boolean isActive;
    private OffsetDateTime createdUtc;
    private int companyId;

    public ReparentProductCategoryResult() {}

    public ReparentProductCategoryResult(int categoryId, String categoryName, String description, Integer parentCategoryId,
                                         boolean isActive, OffsetDateTime createdUtc, int companyId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.parentCategoryId = parentCategoryId;
        this.isActive = isActive;
        this.createdUtc = createdUtc;
        this.companyId = companyId;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getParentCategoryId() { return parentCategoryId; }
    public void setParentCategoryId(Integer parentCategoryId) { this.parentCategoryId = parentCategoryId; }
    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
    public OffsetDateTime getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(OffsetDateTime createdUtc) { this.createdUtc = createdUtc; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
}
