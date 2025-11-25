package com.app.erp.finances.area.domain;

import java.time.OffsetDateTime;

public record Area(
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
