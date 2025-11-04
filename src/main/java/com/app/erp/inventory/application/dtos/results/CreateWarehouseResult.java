package com.app.erp.inventory.application.dtos.results;

import java.time.Instant;

public class CreateWarehouseResult {
    private Integer warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private String address;
    private String phone;
    private String contact;
    private Boolean active;
    private Instant createdUtc;  // UTC
    private Integer branchId;

    public Integer getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Integer warehouseId) { this.warehouseId = warehouseId; }

    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }

    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Instant getCreatedUtc() { return createdUtc; }
    public void setCreatedUtc(Instant createdUtc) { this.createdUtc = createdUtc; }

    public Integer getBranchId() { return branchId; }
    public void setBranchId(Integer branchId) { this.branchId = branchId; }
}
