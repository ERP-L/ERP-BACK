package com.app.erp.finances.invoice.interfaces.rest;

import com.app.erp.finances.invoice.application.service.InvoiceServicePort;
import com.app.erp.finances.invoice.interfaces.rest.requests.InvoiceCreateRequest;
import com.app.erp.finances.invoice.interfaces.rest.responses.InvoiceResponse;
import com.app.erp.finances.invoice.interfaces.rest.responses.InvoiceSearchResponse;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/finances/invoices")
@SecurityRequirement(name = "bearer-jwt")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceServicePort service;
    private final AuthContextResolver authResolver;

    @PostMapping
    public ResponseEntity<Integer> createInvoice(Authentication authentication, @RequestBody InvoiceCreateRequest request) {
        AuthContext auth = authResolver.resolve(authentication);
        int id = service.createInvoice(auth.companyId(), auth.userId(), request);
        URI location = URI.create(String.format("/api/finances/invoices/%d", id));
        return ResponseEntity.created(location).body(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(Authentication authentication, @PathVariable int id) {
        AuthContext auth = authResolver.resolve(authentication);
        InvoiceResponse response = service.findInvoiceById(auth.companyId(), id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InvoiceSearchResponse>> searchInvoices(
        Authentication authentication,
        @RequestParam(required = false) Integer branchId,
        @RequestParam(required = false) Integer costCenterId,
        @RequestParam(required = false) Integer documentTypeId,
        @RequestParam(required = false) String searchTerm,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "20") int pageSize
    ) {
        AuthContext auth = authResolver.resolve(authentication);
        List<InvoiceSearchResponse> results = service.searchInvoices(
            auth.companyId(),
            branchId,
            costCenterId,
            documentTypeId,
            searchTerm,
            pageNumber,
            pageSize
        );
        return ResponseEntity.ok(results);
    }
}
