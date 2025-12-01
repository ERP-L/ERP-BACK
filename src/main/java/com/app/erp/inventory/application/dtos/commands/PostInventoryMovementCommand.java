package com.app.erp.inventory.application.dtos.commands;

import java.time.OffsetDateTime;
import java.util.List;

public class PostInventoryMovementCommand {
    private String movementType; // IN | OUT | TRF | ADJ
    private String lineMode; // NORMAL | BATCH | SERIAL
    private Integer fromWarehouseId;
    private Integer toWarehouseId;
    private Integer purchaseOrderId;
    private Integer temporalId;
    private OffsetDateTime movementDate;
    private List<InventoryLineCommand> lines;
    // options
    private boolean autoCreateBatch;
    private boolean autoCreateSerial;
    private boolean autoCreateLocation;

    public String getMovementType() { return movementType; }
    public void setMovementType(String movementType) { this.movementType = movementType; }
    public String getLineMode() { return lineMode; }
    public void setLineMode(String lineMode) { this.lineMode = lineMode; }
    public Integer getFromWarehouseId() { return fromWarehouseId; }
    public void setFromWarehouseId(Integer fromWarehouseId) { this.fromWarehouseId = fromWarehouseId; }
    public Integer getToWarehouseId() { return toWarehouseId; }
    public void setToWarehouseId(Integer toWarehouseId) { this.toWarehouseId = toWarehouseId; }
    public Integer getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(Integer purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }
    public Integer getTemporalId() { return temporalId; }
    public void setTemporalId(Integer temporalId) { this.temporalId = temporalId; }
    public OffsetDateTime getMovementDate() { return movementDate; }
    public void setMovementDate(OffsetDateTime movementDate) { this.movementDate = movementDate; }
    // createdBy and supplier/customer are intentionally omitted: createdBy comes from JWT; supplier/customer not accepted by API
    public List<InventoryLineCommand> getLines() { return lines; }
    public void setLines(List<InventoryLineCommand> lines) { this.lines = lines; }
    public boolean isAutoCreateBatch() { return autoCreateBatch; }
    public void setAutoCreateBatch(boolean autoCreateBatch) { this.autoCreateBatch = autoCreateBatch; }
    public boolean isAutoCreateSerial() { return autoCreateSerial; }
    public void setAutoCreateSerial(boolean autoCreateSerial) { this.autoCreateSerial = autoCreateSerial; }
    public boolean isAutoCreateLocation() { return autoCreateLocation; }
    public void setAutoCreateLocation(boolean autoCreateLocation) { this.autoCreateLocation = autoCreateLocation; }
}
