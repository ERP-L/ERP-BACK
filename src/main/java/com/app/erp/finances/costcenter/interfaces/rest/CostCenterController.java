package com.app.erp.finances.costcenter.interfaces.rest;

import com.app.erp.finances.costcenter.application.service.CostCenterServicePort;
import com.app.erp.finances.costcenter.interfaces.rest.requests.CostCenterCreateRequest;
import com.app.erp.finances.costcenter.interfaces.rest.responses.CostCenterResponse;
import com.app.erp.inventoryrefactor.common.PageResult;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;


@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/finances/costcenters")
public class CostCenterController {

    private final CostCenterServicePort service;
    private final AuthContextResolver authResolver;

    public CostCenterController(CostCenterServicePort service, AuthContextResolver authResolver) {
        this.service = service;
        this.authResolver = authResolver;
    }

    @PostMapping("/costcenters")
    public ResponseEntity<Integer> create(@RequestBody CostCenterCreateRequest req, Authentication authentication) {
        AuthContext auth = authResolver.resolve(authentication);
        int newId = service.create(auth.companyId(), auth.userId(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(newId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CostCenterResponse> getById(@PathVariable("id") int id, Authentication authentication) {
        var auth = authResolver.resolve(authentication);
        CostCenterResponse resp = service.getById(auth.companyId(), id);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<PageResult<CostCenterResponse>> search(
            @RequestParam(value = "areaId", required = false) Integer areaId,
            @RequestParam(value = "branchId", required = false) Integer branchId,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "25") int pageSize
    , Authentication authentication) {
        var auth = authResolver.resolve(authentication);
        PageResult<CostCenterResponse> result = service.search(auth.companyId(), areaId, branchId, search, page, pageSize);
        return ResponseEntity.ok(result);
    }
}
