package com.app.erp.shared.dtos;

public record ApiError(
        int status,
        String code,      // p.ej. USERNAME_ALREADY_EXISTS
        String message,   // texto legible
        String path,
        String timestamp  // ISO-8601
) {}