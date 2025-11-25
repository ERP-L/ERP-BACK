package com.app.erp.inventoryrefactor.supplier.interfaces.rest;

import com.app.erp.inventoryrefactor.common.PageResult;
import com.app.erp.inventoryrefactor.supplier.application.service.SupplierServicePort;
import com.app.erp.inventoryrefactor.supplier.domain.ProductSummary;
import com.app.erp.inventoryrefactor.supplier.domain.Supplier;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.app.erp.inventoryrefactor.supplier.interfaces.rest.ProductSummaryResponse;
import java.net.URI;
import java.util.List;
import com.app.erp.inventoryrefactor.supplier.interfaces.rest.responses.SupplierResponse;

@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/inventoryR/suppliers")
public class SupplierControllerR {

    private final SupplierServicePort service;
    private final AuthContextResolver authResolver;

    public SupplierControllerR(SupplierServicePort service, AuthContextResolver authResolver) {
        this.service = service;
        this.authResolver = authResolver;
    }

    @PostMapping
    public ResponseEntity<SupplierResponse> create(@RequestBody com.app.erp.inventoryrefactor.supplier.interfaces.rest.SupplierCreateRequest req, Authentication authentication) {
        AuthContext auth = authResolver.resolve(authentication);
        Supplier s = new Supplier(
                0,
                auth.companyId(),
                req.supplierName(),
                req.taxNumber(),
                req.contactName(),
                req.phone(),
                req.email(),
                req.address(),
                req.notes(),
                true
        );
        int id = service.create(auth.companyId(), s);
        SupplierResponse body = new SupplierResponse(id, s.supplierName(), s.taxNumber(), s.contactName(), s.phone(), s.email(), s.address(), s.notes(), s.isActive());
        return ResponseEntity.created(URI.create("/api/inventoryR/suppliers/" + id)).body(body);
    }

        @GetMapping
        public PageResult<com.app.erp.inventoryrefactor.supplier.interfaces.rest.responses.SupplierResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String productIds,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            Authentication authentication) {

        AuthContext auth = authResolver.resolve(authentication);
        PageResult<Supplier> pr = service.getAll(auth.companyId(), search, active, productIds, page, pageSize);
        List<com.app.erp.inventoryrefactor.supplier.interfaces.rest.responses.SupplierResponse> items = pr.items().stream()
            .map(s -> new com.app.erp.inventoryrefactor.supplier.interfaces.rest.responses.SupplierResponse(
                s.supplierId(), s.supplierName(), s.taxNumber(), s.contactName(), s.phone(), s.email(), s.address(), s.notes(), s.isActive()
            ))
            .toList();

        return new PageResult<>(pr.totalCount(), items);
        }


    @GetMapping("/{id}/products")
    public List<ProductSummaryResponse> products(@PathVariable("id") int id,Authentication authentication) {
        AuthContext auth = authResolver.resolve(authentication);
        List<ProductSummary> list = service.getProducts(auth.companyId(), id);
        return list.stream().map(p -> new ProductSummaryResponse(p.productId(), p.sku(), p.productName())).toList();
    }

    @PostMapping("/{id}/products")
    public ResponseEntity<Void> addProducts(@PathVariable("id") int id,
                                            @RequestBody java.util.List<Integer> productIds,
                                            Authentication authentication) {
        AuthContext auth = authResolver.resolve(authentication);
        service.addProducts(auth.companyId(), id, productIds);
        return ResponseEntity.noContent().build();
    }
}
