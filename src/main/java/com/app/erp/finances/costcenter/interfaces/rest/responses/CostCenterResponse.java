package com.app.erp.finances.costcenter.interfaces.rest.responses;

import java.time.OffsetDateTime;

public record CostCenterResponse(
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
