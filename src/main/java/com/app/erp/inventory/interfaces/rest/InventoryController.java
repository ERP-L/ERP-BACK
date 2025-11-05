package com.app.erp.inventory.interfaces.rest;

import com.app.erp.inventory.application.dtos.commands.PostInventoryMovementCommand;
import com.app.erp.inventory.application.dtos.results.PostInventoryMovementResult;
import com.app.erp.inventory.application.usecase.PostInventoryMovementHandler;
import com.app.erp.inventory.application.usecase.ListProductsInWarehouseHandler;
import com.app.erp.inventory.application.usecase.GetProductDetailsInWarehouseHandler;
import com.app.erp.inventory.application.dtos.results.ProductDetailsResult;
import com.app.erp.inventory.interfaces.rest.contracts.InventoryProductResponse;
import java.util.List;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final PostInventoryMovementHandler handler;
    private final ListProductsInWarehouseHandler listHandler;
    private final GetProductDetailsInWarehouseHandler productDetailsHandler;
    private final AuthContextResolver authContextResolver;

    public InventoryController(PostInventoryMovementHandler handler,
                               ListProductsInWarehouseHandler listHandler,
                               GetProductDetailsInWarehouseHandler productDetailsHandler,
                               AuthContextResolver authContextResolver) {
        this.handler = handler;
        this.listHandler = listHandler;
        this.productDetailsHandler = productDetailsHandler;
        this.authContextResolver = authContextResolver;
    }

    @PostMapping("/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public PostInventoryMovementResult postMovement(
            Authentication authentication,
            @RequestBody PostInventoryMovementCommand command) {

        AuthContext auth = authContextResolver.resolve(authentication);
        return handler.handle(command, auth);
    }

    @GetMapping("/warehouses/{warehouseId}/products")
    public List<InventoryProductResponse> listProductsInWarehouse(
            Authentication authentication,
            @PathVariable Integer warehouseId,
            @RequestParam(required = false) Integer productId,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "ProductName") String orderBy,
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "50") Integer pageSize
    ) {
        AuthContext auth = authContextResolver.resolve(authentication);
        return listHandler.handle(auth, warehouseId, productId, categoryId, search, orderBy, pageNumber, pageSize);
    }

    @GetMapping("/warehouses/{warehouseId}/products/{productId}/details")
    public com.app.erp.inventory.interfaces.rest.contracts.ProductStockDetailsResponse getProductDetailsInWarehouse(
            Authentication authentication,
            @PathVariable Integer warehouseId,
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "ExpirationDate") String orderBatch,
            @RequestParam(defaultValue = "1") Integer pageBatch,
            @RequestParam(defaultValue = "100") Integer sizeBatch,
            @RequestParam(defaultValue = "SerialNumber") String orderSerial,
            @RequestParam(defaultValue = "1") Integer pageSerial,
            @RequestParam(defaultValue = "100") Integer sizeSerial
    ) {
        AuthContext auth = authContextResolver.resolve(authentication);
        ProductDetailsResult full = productDetailsHandler.handle(auth,
                warehouseId,
                productId,
                orderBatch,
                pageBatch,
                sizeBatch,
                orderSerial,
                pageSerial,
                sizeSerial);

        com.app.erp.inventory.interfaces.rest.contracts.ProductStockDetailsResponse resp = new com.app.erp.inventory.interfaces.rest.contracts.ProductStockDetailsResponse();

        // The stored procedure returns either batches OR serials (never both). Respect that.
        if (full.getBatches() != null && !full.getBatches().isEmpty()) {
            resp.setBatches(full.getBatches());
            resp.setSerials(null);
        } else if (full.getSerials() != null && !full.getSerials().isEmpty()) {
            resp.setSerials(full.getSerials());
            resp.setBatches(null);
        } else {
            // No details returned: set both to null to avoid misleading clients
            resp.setBatches(null);
            resp.setSerials(null);
        }

        return resp;
    }
}
