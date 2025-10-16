// com.app.erp.organizations.interfaces.rest.resources.BranchResponse
package com.app.erp.organizations.interfaces.rest.resources;

import java.time.OffsetDateTime;

public record BranchResponse(
        int branchId,
        int companyId,
        String name,
        String address,
        String ubigeoId,
        boolean isActive,
        OffsetDateTime createdUtc
) {}
