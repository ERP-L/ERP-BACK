package com.app.erp.iam.application.dto.commands;

public record LoginCommand(
        String email,
        String password,
        String ipAddress,
        String device,
        String userAgent
) {}
