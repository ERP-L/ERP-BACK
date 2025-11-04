package com.app.erp.inventory.interfaces.rest.resources;

import com.app.erp.inventory.application.usecase.CreateWarehouseHandler;
import com.app.erp.inventory.application.dtos.commands.CreateWarehouseCommand;
import com.app.erp.inventory.application.dtos.results.CreateWarehouseResult;
import com.app.erp.inventory.application.usecase.ListWarehousesHandler;
import com.app.erp.inventory.interfaces.rest.contracts.CreateWarehouseRequest;
import com.app.erp.inventory.interfaces.rest.contracts.WarehouseResponse;
import com.app.erp.inventory.interfaces.rest.resources.transformers.WarehouseApiTransformer;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/inventory/warehouses")
public class WarehousesController {

    private final CreateWarehouseHandler service;
    private final WarehouseApiTransformer transformer;
    private final AuthContextResolver authResolver;
    private final ListWarehousesHandler listService;

    public WarehousesController(CreateWarehouseHandler service,
                                WarehouseApiTransformer transformer,
                                AuthContextResolver authResolver,
                                ListWarehousesHandler listService) {
        this.service = service;
        this.transformer = transformer;
        this.authResolver = authResolver;
        this.listService = listService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseResponse create(@RequestBody CreateWarehouseRequest request,
                                    Authentication authentication) { // 👈 Spring te lo inyecta
        AuthContext auth = authResolver.resolve(authentication);     // 👈 pásalo al resolver

        CreateWarehouseCommand cmd = transformer.toCommand(request);
        CreateWarehouseResult res = service.handle(cmd, auth);
        return transformer.toResponse(res);
    }

    @GetMapping
    public java.util.List<com.app.erp.inventory.interfaces.rest.contracts.WarehouseResponse> list(
            @RequestParam(name = "onlyActive", required = false) Boolean onlyActive,
            @RequestParam(name = "branchId", required = false) Integer branchId,
            Authentication authentication) {

        AuthContext auth = authResolver.resolve(authentication);
        var rows = this.listService.handle(auth, onlyActive, branchId);
        return rows.stream().map(transformer::toResponse).toList();
    }
}
