package com.app.erp.finances.purchaseorder.interfaces.rest.requests;

import java.math.BigDecimal;

public record PurchaseOrderProductCreateRequest(
    int productId,
    int warehouseId,
    BigDecimal cantidad,
    BigDecimal unitCost
) {}
