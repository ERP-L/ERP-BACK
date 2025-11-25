package com.app.erp.finances.invoice.interfaces.rest.requests;

import java.math.BigDecimal;

public record InvoiceItemCreateRequest(
    int productId,
    BigDecimal cantidad,
    BigDecimal unitCost
) {}
