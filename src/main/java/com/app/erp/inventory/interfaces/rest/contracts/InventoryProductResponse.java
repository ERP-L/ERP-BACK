package com.app.erp.inventory.interfaces.rest.contracts;

import java.math.BigDecimal;

public class InventoryProductResponse {
    private int productId;
    private String sku;
    private String productName;
    private Integer categoryId;
    private Integer uomId;
    private boolean isSerialized;
    private boolean isBatchControlled;
    private Integer status;
    private String createdUtc;
    private int companyId;
    private String trackingMode;
    private String trackingLabel;
    private BigDecimal avgCost;
    private BigDecimal quantity;
    private BigDecimal reserved;
    private String locationsStr;
    private String updatedUtc;

    public InventoryProductResponse() {}

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
    public String getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(String createdUtc) { this.createdUtc = createdUtc; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public String getTrackingMode() { return trackingMode; }
    public void setTrackingMode(String trackingMode) { this.trackingMode = trackingMode; }
    public String getTrackingLabel() { return trackingLabel; }
    public void setTrackingLabel(String trackingLabel) { this.trackingLabel = trackingLabel; }
    public BigDecimal getAvgCost() { return avgCost; }
    public void setAvgCost(BigDecimal avgCost) { this.avgCost = avgCost; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getReserved() { return reserved; }
    public void setReserved(BigDecimal reserved) { this.reserved = reserved; }
    public String getLocationsStr() { return locationsStr; }
    public void setLocationsStr(String locationsStr) { this.locationsStr = locationsStr; }
    public String getUpdatedUtc() { return updatedUtc; }
    public void setUpdatedUtc(String updatedUtc) { this.updatedUtc = updatedUtc; }
}
