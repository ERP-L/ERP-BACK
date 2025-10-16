package com.app.erp.inventory.interfaces.rest.contracts;

public class CreateWarehouseRequest {
    private Integer branchId;        // requerido
    private String warehouseCode;    // opcional
    private String warehouseName;    // requerido
    private String address;          // opcional
    private String phone;            // opcional
    private String contact;          // opcional

    public Integer getBranchId() { return branchId; }
    public void setBranchId(Integer branchId) { this.branchId = branchId; }

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

}
