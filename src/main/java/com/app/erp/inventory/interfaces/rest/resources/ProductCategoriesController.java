package com.app.erp.inventory.interfaces.rest.resources;

import com.app.erp.inventory.application.internal.commandservices.CreateProductCategoryService;
import com.app.erp.inventory.interfaces.rest.contracts.CreateProductCategoryRequest;
import com.app.erp.inventory.interfaces.rest.contracts.ProductCategoryResponse;
import com.app.erp.inventory.interfaces.rest.transformers.ProductCategoryApiTransformer;
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

    public ProductCategoriesController(CreateProductCategoryService service,
                                       ProductCategoryApiTransformer transformer,
                                       AuthContextResolver authResolver) {
        this.service = service;
        this.transformer = transformer;
        this.authResolver = authResolver;
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
}
