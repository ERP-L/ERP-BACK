package com.app.erp.iam.interfaces.rest.resources;

import com.app.erp.iam.application.dto.results.LoginResult;
import java.util.List;

public record SignInResponse(
        String tokenType,
        String accessToken,
        long   expiresIn,
        Integer securityUserId,
        Integer authUserId,
        Integer companyId,
        List<Integer> globalRoles,
        List<Integer> companyRoles
) {
    public static SignInResponse of(LoginResult r) {
        return new SignInResponse(
                "Bearer",
                r.accessToken(),
                r.expiresInSeconds(),
                r.securityUserId(),
                r.authUserId(),
                r.companyId(),
                r.globalRoles(),
                r.companyRoles()
        );
    }
}
