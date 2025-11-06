package com.app.erp.inventory.application.dtos.results;

import java.time.OffsetDateTime;

public class PostInventoryMovementResult {
    private Long movementId;
    private OffsetDateTime movementDate;
    private String referenceNumber;

    public PostInventoryMovementResult(Long movementId, OffsetDateTime movementDate, String referenceNumber) {
        this.movementId = movementId;
        this.movementDate = movementDate;
        this.referenceNumber = referenceNumber;
    }

    public Long getMovementId() { return movementId; }
    public OffsetDateTime getMovementDate() { return movementDate; }
    public String getReferenceNumber() { return referenceNumber; }
}
