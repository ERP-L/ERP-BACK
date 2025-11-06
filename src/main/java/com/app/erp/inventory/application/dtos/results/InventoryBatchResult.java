package com.app.erp.inventory.application.dtos.results;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class InventoryBatchResult {
    private Integer batchId;
    private String batchNumber;
    private LocalDate manufactureDate;
    private LocalDate expirationDate;
    private OffsetDateTime createdUtc;
    private java.math.BigDecimal quantity;
    private java.math.BigDecimal reserved;
    private String lastLocation;
    private OffsetDateTime updatedUtc;

    public Integer getBatchId() { return batchId; }
    public void setBatchId(Integer batchId) { this.batchId = batchId; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public LocalDate getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(LocalDate manufactureDate) { this.manufactureDate = manufactureDate; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
    public OffsetDateTime getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(OffsetDateTime createdUtc) { this.createdUtc = createdUtc; }
    public java.math.BigDecimal getQuantity() { return quantity; }
    public void setQuantity(java.math.BigDecimal quantity) { this.quantity = quantity; }
    public java.math.BigDecimal getReserved() { return reserved; }
    public void setReserved(java.math.BigDecimal reserved) { this.reserved = reserved; }
    public String getLastLocation() { return lastLocation; }
    public void setLastLocation(String lastLocation) { this.lastLocation = lastLocation; }
    public OffsetDateTime getUpdatedUtc() { return updatedUtc; }
    public void setUpdatedUtc(OffsetDateTime updatedUtc) { this.updatedUtc = updatedUtc; }
}
