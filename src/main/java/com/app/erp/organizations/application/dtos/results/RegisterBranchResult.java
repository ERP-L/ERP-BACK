package com.app.erp.organizations.application.dtos.results;
import java.time.OffsetDateTime;

public record RegisterBranchResult(
        int branchId,
        int companyId,
        String name,
        String address,
        String ubigeoId,
        boolean isActive,
        OffsetDateTime createdUtc
) {}
