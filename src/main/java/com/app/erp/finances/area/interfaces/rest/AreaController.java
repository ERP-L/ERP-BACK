package com.app.erp.finances.area.interfaces.rest;

import com.app.erp.finances.area.application.service.AreaServicePort;
import com.app.erp.finances.area.interfaces.rest.requests.AreaCreateRequest;
import com.app.erp.finances.area.interfaces.rest.responses.AreaResponse;
import com.app.erp.inventoryrefactor.common.PageResult;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/finances")
public class AreaController {

    private final AreaServicePort service;
    private final AuthContextResolver authResolver;

    public AreaController(AreaServicePort service, AuthContextResolver authResolver) {
        this.service = service;
        this.authResolver = authResolver;
    }

    @GetMapping("/areas/{id}")
    public AreaResponse getById(Authentication authentication, @PathVariable("id") int id) {
        AuthContext auth = authResolver.resolve(authentication);
        return service.getById(auth.companyId(), id);
    }

    @GetMapping("/branches/{branchId}/areas")
    public PageResult<AreaResponse> list(
            @PathVariable("branchId") int branchId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            Authentication authentication) {

        AuthContext auth = authResolver.resolve(authentication);
        return service.getByBranch(auth.companyId(), branchId, search, page, pageSize);
    }

    @PostMapping("/areas")
    public ResponseEntity<Integer> create(Authentication authentication, @RequestBody AreaCreateRequest req) {
        AuthContext auth = authResolver.resolve(authentication);
        int id = service.create(auth.companyId(), req);
        return ResponseEntity.created(java.net.URI.create("/api/inventoryR/areas/" + id)).body(id);
    }
}
