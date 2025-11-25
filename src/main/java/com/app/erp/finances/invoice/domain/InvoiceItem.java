package com.app.erp.finances.invoice.domain;

import java.math.BigDecimal;

public record InvoiceItem(
    int invoiceItemId,
    int invoiceId,
    int productId,
    BigDecimal cantidad,
    BigDecimal unitCost
) {}
