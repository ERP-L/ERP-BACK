package com.app.erp.inventory.application.internal.messages.results;

import java.math.BigDecimal;
import java.time.Instant;

/** Resultado del caso de uso (lo que devuelve el SP). */
public class CreateProductResult {

    private Integer productId;
    private String sku;
    private String productName;
    private Integer categoryId;
    private Integer uomId;
    private Boolean isSerialized;
    private Boolean isBatchControlled;
    private BigDecimal reorderLevel;
    private Integer leadTimeDays;
    private BigDecimal weight;
    private BigDecimal volume;
    private Integer status;
    private Instant createdUtc;
    private Instant updatedUtc;
    private Integer companyId;

    // getters/setters
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
    public void setSerialized(Boolean serialized) { isSerialized = serialized; }

    public Boolean getIsBatchControlled() { return isBatchControlled; }
    public void setBatchControlled(Boolean batchControlled) { isBatchControlled = batchControlled; }

    public BigDecimal getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(BigDecimal reorderLevel) { this.reorderLevel = reorderLevel; }

    public Integer getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(Integer leadTimeDays) { this.leadTimeDays = leadTimeDays; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public BigDecimal getVolume() { return volume; }
    public void setVolume(BigDecimal volume) { this.volume = volume; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Instant getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(Instant createdUtc) { this.createdUtc = createdUtc; }

    public Instant getUpdatedUtc() { return updatedUtc; }
    public void setUpdatedUtc(Instant updatedUtc) { this.updatedUtc = updatedUtc; }

    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }
}
