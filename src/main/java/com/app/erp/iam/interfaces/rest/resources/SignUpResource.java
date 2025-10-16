package com.app.erp.iam.interfaces.rest.resources;

public record SignUpResource(
        String username,
        String password,
        String firstName,
        String lastName,
        String email,
        String gender,
        Integer companyId
) {}