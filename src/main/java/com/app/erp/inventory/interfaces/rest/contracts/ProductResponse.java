package com.app.erp.inventory.interfaces.rest.contracts;

public class ProductResponse {
    private Integer productId;
    private String sku;
    private String productName;
    private Integer categoryId;
    private Integer uomId;
    private Boolean isSerialized;
    private Boolean isBatchControlled;
    private java.math.BigDecimal reorderLevel;
    private Integer leadTimeDays;
    private java.math.BigDecimal weight;
    private java.math.BigDecimal volume;
    private Integer status;
    private String createdUtc;   // ISO-8601
    private String updatedUtc;   // ISO-8601
    private Integer companyId;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public Integer getUomId() { return uomId; }
    public void setUomId(Integer uomId) { this.uomId = uomId; }

    public Boolean getIsSerialized() { return isSerialized; }
    public void setIsSerialized(Boolean serialized) { isSerialized = serialized; }

    public Boolean getIsBatchControlled() { return isBatchControlled; }
    public void setIsBatchControlled(Boolean batchControlled) { isBatchControlled = batchControlled; }

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

    public String getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(String createdUtc) { this.createdUtc = createdUtc; }

    public String getUpdatedUtc() { return updatedUtc; }
    public void setUpdatedUtc(String updatedUtc) { this.updatedUtc = updatedUtc; }

    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }
}
