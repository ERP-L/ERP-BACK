package com.app.erp.inventoryrefactor.product.interfaces.rest;

import com.app.erp.inventoryrefactor.product.application.service.ProductServicePort;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/inventoryR")
public class ProductControllerR {

    private final ProductServicePort service;
    private final AuthContextResolver authContextResolver;

    public ProductControllerR(ProductServicePort service, AuthContextResolver authContextResolver) {
        this.service = service;
        this.authContextResolver = authContextResolver;
    }

    @GetMapping("/products/{id}")
    public com.app.erp.inventoryrefactor.product.interfaces.rest.ProductResponse getById(Authentication authentication, @PathVariable("id") int id) {
        AuthContext auth = authContextResolver.resolve(authentication);
        return service.getProductById(auth.companyId(), id);
    }
}
