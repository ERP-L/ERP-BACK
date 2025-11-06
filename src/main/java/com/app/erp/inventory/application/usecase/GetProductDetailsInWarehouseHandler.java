package com.app.erp.inventory.application.usecase;

import com.app.erp.inventory.application.dtos.results.ProductDetailsResult;
import com.app.erp.inventory.application.port.InventoryReadPort;
import com.app.erp.shared.security.AuthContext;
import org.springframework.stereotype.Service;

@Service
public class GetProductDetailsInWarehouseHandler {

    private final InventoryReadPort readPort;

    public GetProductDetailsInWarehouseHandler(InventoryReadPort readPort) {
        this.readPort = readPort;
    }

    public ProductDetailsResult handle(AuthContext auth,
                                       int warehouseId,
                                       int productId,
                                       String orderBatch,
                                       int pageBatch,
                                       int sizeBatch,
                                       String orderSerial,
                                       int pageSerial,
                                       int sizeSerial) {
        return readPort.getProductDetailsInWarehouse(
                warehouseId,
                productId,
                orderBatch,
                pageBatch,
                sizeBatch,
                orderSerial,
                pageSerial,
                sizeSerial,
                auth
        );
    }
}
