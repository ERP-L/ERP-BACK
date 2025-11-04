package com.app.erp.inventory.application.dtos.results;

import java.time.OffsetDateTime;

public class InventoryProductResult {
    private int productId;
    private String sku;
    private String productName;
    private Integer categoryId;
    private Integer uomId;
    private boolean isSerialized;
    private boolean isBatchControlled;
    private Integer status;
    private OffsetDateTime createdUtc;
    private OffsetDateTime updatedUtc;
    private int companyId;
    private String trackingMode;
    private String trackingLabel;
    private java.math.BigDecimal avgCost;
    private java.math.BigDecimal quantity;
    private java.math.BigDecimal reserved;
    private String locationsStr;

    public InventoryProductResult() {}

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
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public OffsetDateTime getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(OffsetDateTime createdUtc) { this.createdUtc = createdUtc; }
    public OffsetDateTime getUpdatedUtc() { return updatedUtc; }
    public void setUpdatedUtc(OffsetDateTime updatedUtc) { this.updatedUtc = updatedUtc; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public String getTrackingMode() { return trackingMode; }
    public void setTrackingMode(String trackingMode) { this.trackingMode = trackingMode; }
    public String getTrackingLabel() { return trackingLabel; }
    public void setTrackingLabel(String trackingLabel) { this.trackingLabel = trackingLabel; }
    public java.math.BigDecimal getAvgCost() { return avgCost; }
    public void setAvgCost(java.math.BigDecimal avgCost) { this.avgCost = avgCost; }
    public java.math.BigDecimal getQuantity() { return quantity; }
    public void setQuantity(java.math.BigDecimal quantity) { this.quantity = quantity; }
    public java.math.BigDecimal getReserved() { return reserved; }
    public void setReserved(java.math.BigDecimal reserved) { this.reserved = reserved; }
    public String getLocationsStr() { return locationsStr; }
    public void setLocationsStr(String locationsStr) { this.locationsStr = locationsStr; }
}
