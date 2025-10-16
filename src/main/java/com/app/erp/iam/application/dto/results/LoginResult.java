package com.app.erp.iam.application.dto.results;

import java.util.List;

public record LoginResult(
        String accessToken,
        long   expiresInSeconds,
        Integer securityUserId,
        Integer authUserId,
        Integer companyId,
        List<Integer> globalRoles,
        List<Integer> companyRoles
) {}
