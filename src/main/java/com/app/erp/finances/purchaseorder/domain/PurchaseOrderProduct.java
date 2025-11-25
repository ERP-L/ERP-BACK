package com.app.erp.finances.purchaseorder.domain;

import java.math.BigDecimal;

public record PurchaseOrderProduct(
    int purchaseOrderProductId,
    int purchaseOrderId,
    int productId,
    int warehouseId,
    BigDecimal cantidad,
    BigDecimal unitCost
) {}
