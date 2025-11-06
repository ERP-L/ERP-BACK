package com.app.erp.inventory.application.dtos.results;

import java.util.List;

public class ProductDetailsResult {
    private InventoryProductResult product;
    private List<InventoryBatchResult> batches;
    private List<InventorySerialResult> serials;

    public InventoryProductResult getProduct() { return product; }
    public void setProduct(InventoryProductResult product) { this.product = product; }
    public List<InventoryBatchResult> getBatches() { return batches; }
    public void setBatches(List<InventoryBatchResult> batches) { this.batches = batches; }
    public List<InventorySerialResult> getSerials() { return serials; }
    public void setSerials(List<InventorySerialResult> serials) { this.serials = serials; }
}
