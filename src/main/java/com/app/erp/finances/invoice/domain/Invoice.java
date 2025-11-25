package com.app.erp.finances.invoice.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record Invoice(
    int invoiceId,
    int companyId,
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
    LocalDateTime dateCreated,
    int userCreated,
    LocalDateTime dateModified,
    Integer userModified,
    int branchId,
    String code,
    String documentTypeName,
    String costCenterName
) {}
