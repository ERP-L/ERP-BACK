package com.app.erp.finances.area.interfaces.rest.responses;

import java.time.OffsetDateTime;

public record AreaResponse(
        int areaId,
        int companyId,
        int branchId,
        String name,
        String code,
        String description,
        Integer userInChargeId,
        OffsetDateTime createdUtc,
        OffsetDateTime updatedUtc
) {}
