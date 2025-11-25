package com.app.erp.finances.purchaseorder.interfaces.rest.requests;

import java.time.LocalDate;
import java.util.List;

public record PurchaseOrderCreateRequest(
    int costCenterId,
    int purchaseTypeId,
    String codigo,
    String ruc,
    String nombre,
    String direccion,
    String telefono,
    LocalDate fechaEmision,
    List<PurchaseOrderProductCreateRequest> products
) {}
