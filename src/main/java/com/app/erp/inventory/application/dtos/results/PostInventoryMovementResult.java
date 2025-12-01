package com.app.erp.inventory.application.dtos.results;

import java.time.OffsetDateTime;

public class PostInventoryMovementResult {
    private Long movementId;
    private OffsetDateTime movementDate;
    private Integer purchaseOrderId;
    private Integer temporalId;

    public PostInventoryMovementResult(Long movementId, OffsetDateTime movementDate, Integer purchaseOrderId, Integer temporalId) {
        this.movementId = movementId;
        this.movementDate = movementDate;
        this.purchaseOrderId = purchaseOrderId;
        this.temporalId = temporalId;
    }

    public Long getMovementId() { return movementId; }
    public OffsetDateTime getMovementDate() { return movementDate; }
    public Integer getPurchaseOrderId() { return purchaseOrderId; }
    public Integer getTemporalId() { return temporalId; }
}
