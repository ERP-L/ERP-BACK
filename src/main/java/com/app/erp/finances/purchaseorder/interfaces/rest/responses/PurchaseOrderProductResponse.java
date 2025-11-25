package com.app.erp.finances.purchaseorder.interfaces.rest.responses;

import java.math.BigDecimal;

public record PurchaseOrderProductResponse(
    int productId,
    int warehouseId,
    BigDecimal cantidad,
    BigDecimal unitCost
) {}
