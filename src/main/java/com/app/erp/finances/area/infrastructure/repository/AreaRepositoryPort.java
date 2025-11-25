package com.app.erp.finances.area.infrastructure.repository;

import com.app.erp.inventoryrefactor.common.PageResult;
import com.app.erp.finances.area.domain.Area;

public interface AreaRepositoryPort {
    Area findById(int companyId, int areaId);

    PageResult<Area> findByBranch(int companyId, int branchId, String search, int page, int pageSize);

    int create(int companyId, Area area);
}
