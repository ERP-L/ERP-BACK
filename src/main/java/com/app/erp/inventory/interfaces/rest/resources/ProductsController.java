package com.app.erp.inventory.interfaces.rest.resources;
import com.app.erp.inventory.application.usecase.CreateProductHandler;
import com.app.erp.inventory.application.dtos.commands.CreateProductCommand;
import com.app.erp.inventory.application.dtos.results.CreateProductResult;
import com.app.erp.inventory.application.usecase.ListProductsHandler;
import com.app.erp.inventory.interfaces.rest.contracts.CreateProductRequest;
import com.app.erp.inventory.interfaces.rest.contracts.ProductResponse;
import com.app.erp.inventory.interfaces.rest.resources.transformers.ProductApiTransformer;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/inventory/products")
public class ProductsController {

    private final CreateProductHandler service;
    private final ProductApiTransformer transformer;
    private final AuthContextResolver authResolver;
    private final ListProductsHandler listService;

    public ProductsController(CreateProductHandler service,
                              ProductApiTransformer transformer,
                              AuthContextResolver authResolver,
                              ListProductsHandler listService) {
        this.service = service;
        this.transformer = transformer;
        this.authResolver = authResolver;
        this.listService = listService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@RequestBody CreateProductRequest request,
                                  Authentication authentication) {
        AuthContext auth = authResolver.resolve(authentication);

        CreateProductCommand cmd = transformer.toCommand(request);
        CreateProductResult res = service.handle(cmd, auth);
        return transformer.toResponse(res);
    }

    @GetMapping
    public java.util.List<ProductResponse> list(Authentication authentication) {
        AuthContext auth = authResolver.resolve(authentication);
        return listService.handle(auth);
    }
}
