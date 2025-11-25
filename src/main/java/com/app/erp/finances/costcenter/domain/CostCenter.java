package com.app.erp.finances.costcenter.domain;

import java.time.OffsetDateTime;

public record CostCenter(
        int costCenterId,
        String code,
        String name,
        String description,
        boolean isActive,
        OffsetDateTime dateCreated,
        Integer userCreated,
        OffsetDateTime dateModified,
        Integer userModified,
        int areaId,
        int companyId
) {}
