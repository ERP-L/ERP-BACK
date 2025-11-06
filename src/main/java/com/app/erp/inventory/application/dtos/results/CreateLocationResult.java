package com.app.erp.inventory.application.dtos.results;

import java.time.Instant;

public class CreateLocationResult {
    private Integer locationId;
    private Integer warehouseId;
    private Integer parentId;
    private String code;
    private Boolean allowStock;
    private Instant createdUtc;

    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }

    public Integer getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Integer warehouseId) { this.warehouseId = warehouseId; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Boolean getAllowStock() { return allowStock; }
    public void setAllowStock(Boolean allowStock) { this.allowStock = allowStock; }

    public Instant getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(Instant createdUtc) { this.createdUtc = createdUtc; }
}
