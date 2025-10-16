package com.app.erp.inventory.interfaces.rest.contracts;

import java.math.BigDecimal;

/**
 * Request de API. NO incluye companyId, status ni createdUtc.
 * Esos los fija el backend en el Service.
 */
public class CreateProductRequest {
    private String sku;                 // opcional
    private String productName;         // requerido
    private Integer categoryId;         // opcional
    private Integer uomId;              // requerido
    private Boolean isSerialized;       // opcional (si null → false en Service)
    private Boolean isBatchControlled;  // opcional (si null → false en Service)
    private BigDecimal reorderLevel;    // opcional
    private Integer leadTimeDays;       // opcional
    private BigDecimal weight;          // opcional
    private BigDecimal volume;          // opcional

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public Integer getUomId() { return uomId; }
    public void setUomId(Integer uomId) { this.uomId = uomId; }

    public Boolean getIsSerialized() { return isSerialized; }
    public void setIsSerialized(Boolean isSerialized) { this.isSerialized = isSerialized; }

    public Boolean getIsBatchControlled() { return isBatchControlled; }
    public void setIsBatchControlled(Boolean isBatchControlled) { this.isBatchControlled = isBatchControlled; }

    public BigDecimal getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(BigDecimal reorderLevel) { this.reorderLevel = reorderLevel; }

    public Integer getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(Integer leadTimeDays) { this.leadTimeDays = leadTimeDays; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public BigDecimal getVolume() { return volume; }
    public void setVolume(BigDecimal volume) { this.volume = volume; }
}
