package com.app.erp.inventory.interfaces.rest.contracts;

public class CreateLocationRequest {
    private String code;
    private Integer parentId;
    private Boolean allowStock;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public Boolean getAllowStock() { return allowStock; }
    public void setAllowStock(Boolean allowStock) { this.allowStock = allowStock; }
}
