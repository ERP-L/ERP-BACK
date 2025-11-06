package com.app.erp.inventory.interfaces.rest.contracts;

import java.time.OffsetDateTime;
import java.util.List;

public class CategoryTreeResponse {
    private Integer categoryId;
    private String categoryName;
    private String description;
    private Integer parentCategoryId;
    private Boolean isActive;
    private OffsetDateTime createdUtc;
    private Integer companyId;
    private List<CategoryTreeResponse> children;

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getParentCategoryId() { return parentCategoryId; }
    public void setParentCategoryId(Integer parentCategoryId) { this.parentCategoryId = parentCategoryId; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public OffsetDateTime getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(OffsetDateTime createdUtc) { this.createdUtc = createdUtc; }
    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }
    public List<CategoryTreeResponse> getChildren() { return children; }
    public void setChildren(List<CategoryTreeResponse> children) { this.children = children; }
}
