package com.app.erp.inventory.interfaces.rest;

import com.app.erp.inventory.application.dtos.commands.PostInventoryMovementCommand;
import com.app.erp.inventory.application.dtos.results.PostInventoryMovementResult;
import com.app.erp.inventory.application.usecase.PostInventoryMovementHandler;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.security.AuthContextResolver;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearer-jwt")
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final PostInventoryMovementHandler handler;
    private final AuthContextResolver authContextResolver;

    public InventoryController(PostInventoryMovementHandler handler, AuthContextResolver authContextResolver) {
        this.handler = handler;
        this.authContextResolver = authContextResolver;
    }

    @PostMapping("/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public PostInventoryMovementResult postMovement(
            Authentication authentication,
            @RequestBody PostInventoryMovementCommand command) {

        AuthContext auth = authContextResolver.resolve(authentication);
        return handler.handle(command, auth);
    }
}
