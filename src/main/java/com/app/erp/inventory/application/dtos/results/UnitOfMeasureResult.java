package com.app.erp.inventory.application.dtos.results;

import java.time.OffsetDateTime;

public class UnitOfMeasureResult {
    private int uomId;
    private String uomCode;
    private String uomName;
    private int decimalPlaces;
    private OffsetDateTime createdUtc;

    public UnitOfMeasureResult() {}

    public int getUomId() { return uomId; }
    public void setUomId(int uomId) { this.uomId = uomId; }
    public String getUomCode() { return uomCode; }
    public void setUomCode(String uomCode) { this.uomCode = uomCode; }
    public String getUomName() { return uomName; }
    public void setUomName(String uomName) { this.uomName = uomName; }
    public int getDecimalPlaces() { return decimalPlaces; }
    public void setDecimalPlaces(int decimalPlaces) { this.decimalPlaces = decimalPlaces; }
    public OffsetDateTime getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(OffsetDateTime createdUtc) { this.createdUtc = createdUtc; }
}
