package com.app.erp.finances.purchaseorder.interfaces.rest.responses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PurchaseOrderResponse(
    int purchaseOrderId,
    String codigo,
    int companyId,
    int costCenterId,
    String costCenterName,
    String ruc,
    String nombre,
    String direccion,
    String telefono,
    LocalDate fechaEmision,
    BigDecimal total,
    String purchaseType,
    String status,
    List<PurchaseOrderProductResponse> products
) {}
