package com.app.erp.inventoryrefactor.supplier.interfaces.rest;

public record SupplierCreateRequest(
	String supplierName,
	String taxNumber,
	String contactName,
	String phone,
	String email,
	String address,
	String notes
) {}
