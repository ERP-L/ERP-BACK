package com.app.erp.inventory.interfaces.rest;

import com.app.erp.inventory.application.dtos.commands.PostInventoryMovementCommand;
import com.app.erp.inventory.application.dtos.results.PostInventoryMovementResult;
import com.app.erp.inventory.application.usecase.PostInventoryMovementHandler;
import com.app.erp.inventory.application.usecase.ListProductsInWarehouseHandler;
import com.app.erp.inventory.application.usecase.GetProductDetailsInWarehouseHandler;
import com.app.erp.inventory.application.usecase.GetRecentMovementsHandler;
import com.app.erp.inventory.application.usecase.CreateLocationHandler;
import com.app.erp.inventory.application.usecase.ListLocationsHandler;
import com.app.erp.inventory.application.dtos.results.RecentMovementResult;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import com.app.erp.inventory.application.dtos.results.ProductDetailsResult;
import com.app.erp.inventory.interfaces.rest.contracts.InventoryProductResponse;
import java.util.List;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.app.erp.inventory.interfaces.rest.contracts.CreateLocationRequest;
import com.app.erp.inventory.interfaces.rest.contracts.LocationResponse;
import com.app.erp.inventory.interfaces.rest.resources.transformers.LocationApiTransformer;
import java.util.stream.Collectors;

@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final PostInventoryMovementHandler handler;
    private final ListProductsInWarehouseHandler listHandler;
    private final GetProductDetailsInWarehouseHandler productDetailsHandler;
    private final GetRecentMovementsHandler recentHandler;
    private final CreateLocationHandler createLocationHandler;
    private final ListLocationsHandler listLocationsHandler;
    private final AuthContextResolver authContextResolver;
    private final LocationApiTransformer locationApiTransformer;

    public InventoryController(PostInventoryMovementHandler handler,
                               ListProductsInWarehouseHandler listHandler,
                               GetProductDetailsInWarehouseHandler productDetailsHandler,
                               GetRecentMovementsHandler recentHandler,
                               AuthContextResolver authContextResolver,
                               CreateLocationHandler createLocationHandler,
                               ListLocationsHandler listLocationsHandler,
                               LocationApiTransformer locationApiTransformer) {
        this.handler = handler;
        this.listHandler = listHandler;
        this.productDetailsHandler = productDetailsHandler;
        this.recentHandler = recentHandler;
        this.authContextResolver = authContextResolver;
        this.createLocationHandler = createLocationHandler;
        this.listLocationsHandler = listLocationsHandler;
        this.locationApiTransformer = locationApiTransformer;
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

    @GetMapping("/movements/recent")
    public java.util.List<RecentMovementResult> getRecentMovements(
            Authentication authentication,
            @RequestParam(required = false) Integer warehouseId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        AuthContext auth = authContextResolver.resolve(authentication);
        return recentHandler.handle(auth, warehouseId, search, dateFrom, dateTo, type, page, size);
    }

    @PostMapping("/warehouses/{warehouseId}/locations")
    @ResponseStatus(HttpStatus.CREATED)
    public LocationResponse createLocation(
            Authentication authentication,
            @PathVariable Integer warehouseId,
            @RequestBody CreateLocationRequest req
    ) {
        AuthContext auth = authContextResolver.resolve(authentication);
        com.app.erp.inventory.application.dtos.commands.CreateLocationCommand cmd = locationApiTransformer.toCommand(req);
        cmd.setWarehouseId(warehouseId);
        com.app.erp.inventory.application.dtos.results.CreateLocationResult res = createLocationHandler.handle(cmd, auth);
        return locationApiTransformer.toResponse(res);
    }

    @GetMapping("/warehouses/{warehouseId}/locations")
    public java.util.List<LocationResponse> listLocations(
            Authentication authentication,
            @PathVariable Integer warehouseId,
            @RequestParam(required = false) Boolean onlyAllowStock
    ) {
        AuthContext auth = authContextResolver.resolve(authentication);
        java.util.List<com.app.erp.inventory.application.dtos.results.LocationResult> list = listLocationsHandler.handle(auth, warehouseId, onlyAllowStock);
        return list.stream().map(locationApiTransformer::toResponse).collect(Collectors.toList());
    }
}
