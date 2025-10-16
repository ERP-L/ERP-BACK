package com.app.erp.iam.application.internal.outboundservices.auth;

import com.app.erp.iam.application.dto.commands.LoginCommand;
import com.app.erp.iam.application.dto.commands.RegisterCommand;
import com.app.erp.iam.application.dto.results.LoginResult;
import com.app.erp.iam.application.dto.results.RegisterResult;

public interface AuthGateway {
    RegisterResult register(RegisterCommand cmd);
    LoginResult login(LoginCommand cmd);
}
