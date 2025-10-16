package com.app.erp.iam.application.usecase;

import com.app.erp.iam.application.dto.commands.LoginCommand;
import com.app.erp.iam.application.dto.results.LoginResult;
import com.app.erp.iam.application.internal.outboundservices.auth.AuthGateway;
import com.app.erp.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.stereotype.Service;

@Service
public class LoginHandler {

    private final AuthGateway authGateway;

    public LoginHandler(AuthGateway authGateway) {
        this.authGateway = authGateway;
    }

    public LoginResult handle(LoginCommand cmd) {
        // No re-hasheamos aquí, solo delegamos
        return authGateway.login(cmd);
    }
}