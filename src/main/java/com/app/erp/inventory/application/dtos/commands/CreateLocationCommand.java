package com.app.erp.inventory.application.dtos.commands;

public class CreateLocationCommand {
    private Integer warehouseId;
    private String code;
    private Integer parentId;
    private Boolean allowStock;

    public Integer getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Integer warehouseId) { this.warehouseId = warehouseId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public Boolean getAllowStock() { return allowStock; }
    public void setAllowStock(Boolean allowStock) { this.allowStock = allowStock; }
}
