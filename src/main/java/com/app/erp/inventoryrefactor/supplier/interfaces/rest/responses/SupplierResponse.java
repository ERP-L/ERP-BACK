package com.app.erp.inventoryrefactor.supplier.interfaces.rest.responses;

public record SupplierResponse(
	int supplierId,
	String supplierName,
	String taxNumber,
	String contactName,
	String phone,
	String email,
	String address,
	String notes,
	boolean isActive
) {}
