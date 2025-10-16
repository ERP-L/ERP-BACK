package com.app.erp.iam.interfaces.rest.resources;

public record AuthenticatedUserResource(
        String username,
        String token,
        Integer securityUserId,
        Integer authUserId
) {}