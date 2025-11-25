package com.app.erp.finances.area.application.service;

import com.app.erp.finances.area.interfaces.rest.requests.AreaCreateRequest;
import com.app.erp.finances.area.interfaces.rest.responses.AreaResponse;
import com.app.erp.inventoryrefactor.common.PageResult;

public interface AreaServicePort {
    AreaResponse getById(int companyId, int areaId);

    PageResult<AreaResponse> getByBranch(int companyId, int branchId, String search, int page, int pageSize);

    int create(int companyId, AreaCreateRequest req);
}
