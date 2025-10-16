// com.app.erp.organizations.interfaces.rest.BranchController
package com.app.erp.organizations.interfaces.rest;

import com.app.erp.organizations.application.dtos.results.RegisterBranchResult;
import com.app.erp.organizations.application.usecase.RegisterBranchHandler;
import com.app.erp.organizations.interfaces.rest.resources.BranchResponse;
import com.app.erp.organizations.interfaces.rest.resources.CreateBranchRequest;
import com.app.erp.organizations.interfaces.rest.transform.BranchTransform;
import com.app.erp.shared.exceptions.AuthorizationException;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/org/branches")
public class BranchController {

    private final RegisterBranchHandler handler;
    private final AuthContextResolver authResolver;
    private final BranchTransform transform;

    public BranchController(RegisterBranchHandler handler,
                            AuthContextResolver authResolver,
                            BranchTransform transform) {
        this.handler = handler;
        this.authResolver = authResolver;
        this.transform = transform;
    }


    @PostMapping
    public ResponseEntity<BranchResponse> create(@Valid @RequestBody CreateBranchRequest body,
                                                 Authentication authentication) {
        AuthContext ctx = authResolver.resolve(authentication);
        if (ctx.companyId() == null) {
            throw new AuthorizationException("Tu token no tiene compañía asociada (cid).");
        }

        var cmd = transform.toCommand(ctx, body);
        RegisterBranchResult result = handler.handle(ctx, cmd);

        URI location = URI.create("/org/branches/" + result.branchId());
        return ResponseEntity.created(location).body(transform.toResponse(result));
    }
}
