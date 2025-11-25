package com.app.erp.finances.invoice.interfaces.rest.requests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record InvoiceCreateRequest(
    int documentTypeId,
    String ruc,
    String serie,
    String numeroFactura,
    BigDecimal igv,
    BigDecimal total,
    LocalDate fecha,
    String urlDocumento,
    Integer purchaseOrderId,
    Integer costCenterId,
    int branchId,
    String code,
    List<InvoiceItemCreateRequest> items
) {}
