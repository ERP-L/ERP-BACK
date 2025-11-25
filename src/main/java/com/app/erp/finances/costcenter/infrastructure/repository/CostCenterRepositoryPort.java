package com.app.erp.finances.costcenter.infrastructure.repository;

import com.app.erp.inventoryrefactor.common.PageResult;
import com.app.erp.finances.costcenter.domain.CostCenter;

public interface CostCenterRepositoryPort {
    int create(int companyId, int userCreated, CostCenter cc);

    CostCenter findById(int companyId, int costCenterId);

    PageResult<CostCenter> search(int companyId, Integer areaId, Integer branchId, String search, int page, int pageSize);
}
