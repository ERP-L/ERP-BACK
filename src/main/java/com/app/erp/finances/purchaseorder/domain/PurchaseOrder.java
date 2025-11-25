package com.app.erp.finances.purchaseorder.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PurchaseOrder(
    int purchaseOrderId,
    int companyId,
    int costCenterId,
    int purchaseTypeId,
    String codigo,
    String ruc,
    String nombre,
    String direccion,
    String telefono,
    LocalDate fechaEmision,
    BigDecimal total,
    int statusId,
    int userCreated,
    LocalDateTime dateCreated,
    Integer userModified,
    LocalDateTime dateModified,
    String costCenterName,
    String purchaseType,
    String status
) {}
