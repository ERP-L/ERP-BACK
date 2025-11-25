package com.app.erp.finances.purchaseorder.interfaces.rest;

import com.app.erp.finances.purchaseorder.application.service.PurchaseOrderServicePort;
import com.app.erp.finances.purchaseorder.interfaces.rest.requests.PurchaseOrderCreateRequest;
import com.app.erp.finances.purchaseorder.interfaces.rest.responses.PurchaseOrderResponse;
import com.app.erp.finances.purchaseorder.interfaces.rest.responses.PurchaseOrderSearchResponse;
import com.app.erp.shared.dtos.PagedResponse;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/finances/purchase-orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-jwt")
public class PurchaseOrderController {

    private final PurchaseOrderServicePort purchaseOrderService;
    private final AuthContextResolver authContextResolver;

    @PostMapping
    public ResponseEntity<Integer> createPurchaseOrder(Authentication authentication, @RequestBody PurchaseOrderCreateRequest request) {
        AuthContext authContext = authContextResolver.resolve(authentication);
        int id = purchaseOrderService.createPurchaseOrder(authContext.companyId(), authContext.userId(), request);
        URI location = URI.create(String.format("/api/finances/purchase-orders/%d", id));
        return ResponseEntity.created(location).body(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrderResponse> getPurchaseOrderById(Authentication authentication, @PathVariable int id) {
        AuthContext authContext = authContextResolver.resolve(authentication);
        PurchaseOrderResponse response = purchaseOrderService.findPurchaseOrderById(authContext.companyId(), id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<PurchaseOrderSearchResponse>> searchPurchaseOrders(
            Authentication authentication,
            @RequestParam(required = false) Integer areaId,
            @RequestParam(required = false) Integer costCenterId,
            @RequestParam(required = false) String purchaseTypeCode,
            @RequestParam(required = false) String statusCode,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {
        AuthContext authContext = authContextResolver.resolve(authentication);
        PagedResponse<PurchaseOrderSearchResponse> response = purchaseOrderService.searchPurchaseOrders(authContext.companyId(), areaId, costCenterId, purchaseTypeCode, statusCode, searchTerm, pageNumber, pageSize);
        return ResponseEntity.ok(response);
    }
}
