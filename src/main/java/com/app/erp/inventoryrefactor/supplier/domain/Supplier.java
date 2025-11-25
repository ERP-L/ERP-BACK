package com.app.erp.inventoryrefactor.supplier.domain;

public record Supplier(
	int supplierId,
	int companyId,
	String supplierName,
	String taxNumber,
	String contactName,
	String phone,
	String email,
	String address,
	String notes,
	boolean isActive
) {}
