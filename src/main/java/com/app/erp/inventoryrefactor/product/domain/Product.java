package com.app.erp.inventoryrefactor.product.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record Product(
    int productId,
    String sku,
    String productName,
    Integer categoryId,
    Integer uomId,
    boolean isSerialized,
    boolean isBatchControlled,
    BigDecimal reorderLevel,
    Integer leadTimeDays,
    BigDecimal weight,
    BigDecimal volume,
    String status,
    OffsetDateTime createdUtc,
    OffsetDateTime updatedUtc,
    int companyId
) {}
