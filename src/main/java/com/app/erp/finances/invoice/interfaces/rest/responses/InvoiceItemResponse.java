package com.app.erp.finances.invoice.interfaces.rest.responses;

import java.math.BigDecimal;

public record InvoiceItemResponse(
    int invoiceItemId,
    int productId,
    BigDecimal cantidad,
    BigDecimal unitCost
) {}
