package com.app.erp.inventory.interfaces.rest.contracts;

import com.app.erp.inventory.application.dtos.results.InventoryBatchResult;
import com.app.erp.inventory.application.dtos.results.InventorySerialResult;
import java.util.List;

public class ProductStockDetailsResponse {
    private List<InventoryBatchResult> batches;
    private List<InventorySerialResult> serials;

    public List<InventoryBatchResult> getBatches() { return batches; }
    public void setBatches(List<InventoryBatchResult> batches) { this.batches = batches; }
    public List<InventorySerialResult> getSerials() { return serials; }
    public void setSerials(List<InventorySerialResult> serials) { this.serials = serials; }
}
