package com.app.erp.finances.costcenter.application.service;

import com.app.erp.inventoryrefactor.common.PageResult;
import com.app.erp.finances.costcenter.interfaces.rest.requests.CostCenterCreateRequest;
import com.app.erp.finances.costcenter.interfaces.rest.responses.CostCenterResponse;

public interface CostCenterServicePort {
    int create(int companyId, int userCreated, CostCenterCreateRequest req);

    CostCenterResponse getById(int companyId, int costCenterId);

    PageResult<CostCenterResponse> search(int companyId, Integer areaId, Integer branchId, String search, int page, int pageSize);
}
