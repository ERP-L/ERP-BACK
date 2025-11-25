package com.app.erp.finances.purchaseorder.interfaces.rest.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PurchaseOrderSearchResponse(
    int purchaseOrderId,
    String codigo,
    int companyId,
    int costCenterId,
    String costCenterName,
    String costCenterCode,
    String ruc,
    String nombre,
    String direccion,
    String telefono,
    LocalDate fechaEmision,
    BigDecimal total,
    String purchaseType,
    String status,
    LocalDateTime dateCreated,
    int userCreated,
    Integer totalRows    // nullable: viene de COUNT(1) OVER() AS TotalRows del SP
) {}
