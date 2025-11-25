package com.app.erp.finances.area.application.service;

import com.app.erp.finances.area.domain.Area;
import com.app.erp.finances.area.infrastructure.repository.AreaRepositoryPort;
import com.app.erp.finances.area.interfaces.rest.requests.AreaCreateRequest;
import com.app.erp.finances.area.interfaces.rest.responses.AreaResponse;
import com.app.erp.inventoryrefactor.common.PageResult;
import org.springframework.stereotype.Service;
import com.app.erp.shared.security.AuthContextResolver;

import java.util.List;

@Service
public class AreaServiceImpl implements AreaServicePort {

    private final AreaRepositoryPort repository;
    private final AuthContextResolver authResolver;

    public AreaServiceImpl(AreaRepositoryPort repository, AuthContextResolver authResolver) {
        this.repository = repository;
        this.authResolver = authResolver;
    }

    @Override
    public AreaResponse getById(int companyId, int areaId) {
        Area a = repository.findById(companyId, areaId);
        if (a == null) return null;
        return new AreaResponse(
                a.areaId(), a.companyId(), a.branchId(), a.name(), a.code(), a.description(), a.userInChargeId(), a.createdUtc(), a.updatedUtc()
        );
    }

    @Override
    public PageResult<AreaResponse> getByBranch(int companyId, int branchId, String search, int page, int pageSize) {
        PageResult<Area> pr = repository.findByBranch(companyId, branchId, search, page, pageSize);
        List<AreaResponse> items = pr.items().stream()
                .map(a -> new AreaResponse(
                        a.areaId(), a.companyId(), a.branchId(), a.name(), a.code(), a.description(), a.userInChargeId(), a.createdUtc(), a.updatedUtc()
                ))
                .toList();
        return new PageResult<>(pr.totalCount(), items);
    }

    @Override
    public int create(int companyId, AreaCreateRequest req) {
        Area a = new Area(0, companyId, req.branchId(), req.name(), req.code(), req.description(), req.userInChargeId(), null, null);
        return repository.create(companyId, a);
    }
}
