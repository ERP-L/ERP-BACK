package com.app.erp.inventory.interfaces.rest;

import com.app.erp.inventory.application.dtos.commands.PostInventoryMovementCommand;
import com.app.erp.inventory.application.dtos.results.PostInventoryMovementResult;
import com.app.erp.inventory.application.usecase.PostInventoryMovementHandler;
import com.app.erp.inventory.application.usecase.ListProductsInWarehouseHandler;
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
    private final AuthContextResolver authContextResolver;

    public InventoryController(PostInventoryMovementHandler handler,
                               ListProductsInWarehouseHandler listHandler,
                               AuthContextResolver authContextResolver) {
        this.handler = handler;
        this.listHandler = listHandler;
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
}
