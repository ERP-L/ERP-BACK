package com.app.erp.inventory.application.internal.messages.results;

import java.time.OffsetDateTime;

public class ProductResult {
    private int productId;
    private String sku;
    private String productName;
    private Integer categoryId;
    private Integer uomId;
    private boolean isSerialized;
    private boolean isBatchControlled;
    private java.math.BigDecimal reorderLevel;
    private Integer leadTimeDays;
    private java.math.BigDecimal weight;
    private java.math.BigDecimal volume;
    private Integer status;
    private OffsetDateTime createdUtc;
    private OffsetDateTime updatedUtc;
    private int companyId;

    public ProductResult() {}

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getUomId() { return uomId; }
    public void setUomId(Integer uomId) { this.uomId = uomId; }
    public boolean getIsSerialized() { return isSerialized; }
    public void setIsSerialized(boolean isSerialized) { this.isSerialized = isSerialized; }
    public boolean getIsBatchControlled() { return isBatchControlled; }
    public void setIsBatchControlled(boolean isBatchControlled) { this.isBatchControlled = isBatchControlled; }
    public java.math.BigDecimal getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(java.math.BigDecimal reorderLevel) { this.reorderLevel = reorderLevel; }
    public Integer getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(Integer leadTimeDays) { this.leadTimeDays = leadTimeDays; }
    public java.math.BigDecimal getWeight() { return weight; }
    public void setWeight(java.math.BigDecimal weight) { this.weight = weight; }
    public java.math.BigDecimal getVolume() { return volume; }
    public void setVolume(java.math.BigDecimal volume) { this.volume = volume; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public OffsetDateTime getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(OffsetDateTime createdUtc) { this.createdUtc = createdUtc; }
    public OffsetDateTime getUpdatedUtc() { return updatedUtc; }
    public void setUpdatedUtc(OffsetDateTime updatedUtc) { this.updatedUtc = updatedUtc; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
}
