package com.app.erp.finances.invoice.interfaces.rest.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InvoiceSearchResponse(
    int invoiceId,
    int companyId,
    int branchId,
    int documentTypeId,
    String documentTypeName,
    String ruc,
    String serie,
    String numeroFactura,
    String code,
    BigDecimal igv,
    BigDecimal total,
    LocalDate fecha,
    String urlDocumento,
    Integer purchaseOrderId,
    Integer costCenterId,
    String costCenterName,
    LocalDateTime dateCreated,
    int userCreated,
    LocalDateTime dateModified,
    Integer userModified,
    int totalRows
) {}
