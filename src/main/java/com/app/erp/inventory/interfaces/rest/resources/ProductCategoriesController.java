package com.app.erp.inventory.interfaces.rest.resources;

import com.app.erp.inventory.application.internal.commandservices.CreateProductCategoryService;
import com.app.erp.inventory.application.internal.commandservices.ReparentProductCategoryService;
import com.app.erp.inventory.application.internal.queryservices.ListProductCategoriesService;
import com.app.erp.inventory.interfaces.rest.contracts.CategoryTreeResponse;
import com.app.erp.inventory.interfaces.rest.contracts.CreateProductCategoryRequest;
import com.app.erp.inventory.interfaces.rest.contracts.ProductCategoryResponse;
import com.app.erp.inventory.interfaces.rest.transformers.ProductCategoryApiTransformer;
import org.springframework.http.ResponseEntity;
import java.util.List;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/inventory/categories")
public class ProductCategoriesController {

    private final CreateProductCategoryService service;
    private final ProductCategoryApiTransformer transformer;
    private final AuthContextResolver authResolver;
    private final ReparentProductCategoryService reparentService;
    private final ListProductCategoriesService listService;

    public ProductCategoriesController(CreateProductCategoryService service,
                                       ProductCategoryApiTransformer transformer,
                                       AuthContextResolver authResolver,
                                       ReparentProductCategoryService reparentService,
                                       ListProductCategoriesService listService) {
        this.service = service;
        this.transformer = transformer;
        this.authResolver = authResolver;
        this.reparentService = reparentService;
        this.listService = listService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCategoryResponse create(@RequestBody CreateProductCategoryRequest req,
                                          Authentication authentication) {
        AuthContext auth = authResolver.resolve(authentication);
        var cmd = transformer.toCommand(req);
        var res = service.handle(cmd, auth);
        return transformer.toResponse(res);
    }

    @PatchMapping("/{categoryId}/parent")
    public com.app.erp.inventory.interfaces.rest.contracts.ReparentProductCategoryResponse reparent(
            @PathVariable("categoryId") int categoryId,
            @RequestBody com.app.erp.inventory.interfaces.rest.contracts.ReparentProductCategoryRequest req,
            Authentication authentication) {

        AuthContext auth = authResolver.resolve(authentication);
        var cmd = new com.app.erp.inventory.application.internal.messages.commands.ReparentProductCategoryCommand(categoryId, req.getNewParentCategoryId());
        var res = reparentService.handle(cmd, auth);
        return transformer.toReparentResponse(res);
    }

    @GetMapping
    public ResponseEntity<List<CategoryTreeResponse>> list(@RequestParam(name = "onlyActive", required = false) Boolean onlyActive,
                                                           org.springframework.security.core.Authentication authentication) {
        com.app.erp.shared.security.AuthContext auth = authResolver.resolve(authentication);
        List<CategoryTreeResponse> result = listService.listAll(auth, onlyActive);
        return ResponseEntity.ok(result);
    }
}
