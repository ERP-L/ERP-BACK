package com.app.erp.inventory.interfaces.rest.contracts;

public class LocationResponse {
    private Integer locationId;
    private Integer warehouseId;
    private Integer parentId;
    private String parentCode;
    private String code;
    private Boolean allowStock;
    private String createdUtc;

    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }

    public Integer getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Integer warehouseId) { this.warehouseId = warehouseId; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public String getParentCode() { return parentCode; }
    public void setParentCode(String parentCode) { this.parentCode = parentCode; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Boolean getAllowStock() { return allowStock; }
    public void setAllowStock(Boolean allowStock) { this.allowStock = allowStock; }

    public String getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(String createdUtc) { this.createdUtc = createdUtc; }
}
