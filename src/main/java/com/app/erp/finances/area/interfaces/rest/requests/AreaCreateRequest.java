package com.app.erp.finances.area.interfaces.rest.requests;

public record AreaCreateRequest(
        int branchId,
        String name,
        String code,
        String description,
        Integer userInChargeId
) {}
