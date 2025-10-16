package com.app.erp.iam.interfaces.rest.resources;

public record SignInRequest(
        String email,
        String password
) {}
