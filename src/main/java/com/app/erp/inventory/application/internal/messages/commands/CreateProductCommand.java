package com.app.erp.inventory.application.internal.messages.commands;
import java.math.BigDecimal;
import java.time.Instant;

/** Mensaje de entrada del caso de uso (nivel aplicación, no HTTP). */
public class CreateProductCommand {

    private Integer companyId;          // lo fija la app desde el token
    private String sku;                 // opcional
    private String productName;         // requerido
    private Integer categoryId;         // opcional (se valida por compañía si viene)
    private Integer uomId;              // requerido
    private Boolean isSerialized;       // app normaliza null→false
    private Boolean isBatchControlled;  // app normaliza null→false
    private BigDecimal reorderLevel;    // opcional
    private Integer leadTimeDays;       // opcional
    private BigDecimal weight;          // opcional
    private BigDecimal volume;          // opcional
    private Integer status;             // lo fija la app = 1
    private Instant createdUtc;         // lo fija la app = Instant.now()

    // getters/setters
    public Integer getCompanyId() { return companyId; }
    public void setCompanyId(Integer companyId) { this.companyId = companyId; }

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

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Instant getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(Instant createdUtc) { this.createdUtc = createdUtc; }
}
