package com.app.erp.inventory.application.dtos.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InventoryLineCommand {
    private Integer productId;
    private Integer batchId;
    private String batchNumber;
    private LocalDate batchManufactureDate;
    private LocalDate batchExpirationDate;
    private Integer serialId;
    private String serialNumber;
    private BigDecimal quantity;
    private BigDecimal unitCost;
    private String notes;
    private String locationCode;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public Integer getBatchId() { return batchId; }
    public void setBatchId(Integer batchId) { this.batchId = batchId; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public LocalDate getBatchManufactureDate() { return batchManufactureDate; }
    public void setBatchManufactureDate(LocalDate batchManufactureDate) { this.batchManufactureDate = batchManufactureDate; }
    public LocalDate getBatchExpirationDate() { return batchExpirationDate; }
    public void setBatchExpirationDate(LocalDate batchExpirationDate) { this.batchExpirationDate = batchExpirationDate; }
    public Integer getSerialId() { return serialId; }
    public void setSerialId(Integer serialId) { this.serialId = serialId; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }
}
