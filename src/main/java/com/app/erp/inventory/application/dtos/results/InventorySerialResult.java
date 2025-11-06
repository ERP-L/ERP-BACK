package com.app.erp.inventory.application.dtos.results;

import java.time.OffsetDateTime;

public class InventorySerialResult {
    private Integer serialId;
    private String serialNumber;
    private java.math.BigDecimal unitCost;
    private OffsetDateTime createdUtc;
    private String lastLocation;
    private OffsetDateTime updatedUtc;

    public Integer getSerialId() { return serialId; }
    public void setSerialId(Integer serialId) { this.serialId = serialId; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public java.math.BigDecimal getUnitCost() { return unitCost; }
    public void setUnitCost(java.math.BigDecimal unitCost) { this.unitCost = unitCost; }
    public OffsetDateTime getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(OffsetDateTime createdUtc) { this.createdUtc = createdUtc; }
    public String getLastLocation() { return lastLocation; }
    public void setLastLocation(String lastLocation) { this.lastLocation = lastLocation; }
    public OffsetDateTime getUpdatedUtc() { return updatedUtc; }
    public void setUpdatedUtc(OffsetDateTime updatedUtc) { this.updatedUtc = updatedUtc; }
}
