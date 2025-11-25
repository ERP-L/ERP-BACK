package com.app.erp.finances.costcenter.interfaces.rest.requests;

public record CostCenterCreateRequest(
        int areaId,
        String code,
        String name,
        String description
) {}
