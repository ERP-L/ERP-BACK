package com.app.erp.iam.application.usecase;

import com.app.erp.iam.application.dto.commands.RegisterCommand;
import com.app.erp.iam.application.dto.results.RegisterResult;
import com.app.erp.iam.application.internal.outboundservices.auth.AuthGateway;
import com.app.erp.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserHandler {

    private final HashingService hashingService;
    private final AuthGateway authGateway;

    public RegisterUserHandler(HashingService hashingService,
                               AuthGateway authGateway) {
        this.hashingService = hashingService;
        this.authGateway = authGateway;
    }

    public RegisterResult handle(RegisterCommand cmd) {
        if (cmd == null || cmd.getCompany() == null || cmd.getSecurityUser() == null || cmd.getTenantUser() == null) {
            throw new IllegalArgumentException("SIGN_UP_REQUEST_MALFORMED");
        }
        if (cmd.getCompany().getDocumentTypeId() == null || cmd.getCompany().getDocumentTypeId() != 3) {
            throw new IllegalArgumentException("COMPANY_DOCUMENT_TYPE_MUST_BE_3");
        }

        // 1) Hash (String) según tu HashingService
        String encoded = hashingService.encode(cmd.getSecurityUser().getRawPassword());

        // 2) Guardar el hash en el mismo command (campo nuevo)
        cmd.getSecurityUser().setEncodedPassword(encoded);

        // 3) Invocar al SP vía gateway
        return authGateway.register(cmd);
    }
}
