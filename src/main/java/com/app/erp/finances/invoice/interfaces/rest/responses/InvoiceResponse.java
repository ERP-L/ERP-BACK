package com.app.erp.finances.invoice.interfaces.rest.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InvoiceResponse(
    int invoiceId,
    int companyId,
    int documentTypeId,
    String documentTypeName,
    String ruc,
    String serie,
    String numeroFactura,
    BigDecimal igv,
    BigDecimal total,
    LocalDate fecha,
    String urlDocumento,
    Integer purchaseOrderId,
    Integer costCenterId,
    String costCenterName,
    int branchId,
    String code,
    LocalDateTime dateCreated,
    int userCreated,
    List<InvoiceItemResponse> items
) {}
