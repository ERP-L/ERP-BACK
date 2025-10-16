package com.app.erp.inventory.interfaces.rest.resources;

import com.app.erp.inventory.application.internal.commandservices.CreateWarehouseService;
import com.app.erp.inventory.application.internal.messages.commands.CreateWarehouseCommand;
import com.app.erp.inventory.application.internal.messages.results.CreateWarehouseResult;
import com.app.erp.inventory.application.internal.queryservices.ListWarehousesService;
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

    private final CreateWarehouseService service;
    private final WarehouseApiTransformer transformer;
    private final AuthContextResolver authResolver;
    private final ListWarehousesService listWarehousesService;

    public WarehousesController(CreateWarehouseService service,
                                WarehouseApiTransformer transformer,
                                AuthContextResolver authResolver,
                                ListWarehousesService listWarehousesService) {
        this.service = service;
        this.transformer = transformer;
        this.authResolver = authResolver;
        this.listWarehousesService = listWarehousesService;
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
        var rows = listWarehousesService.handle(auth, onlyActive, branchId);
        return rows.stream().map(transformer::toResponse).toList();
    }
}
