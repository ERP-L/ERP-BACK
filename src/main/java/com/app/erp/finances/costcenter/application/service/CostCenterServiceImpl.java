package com.app.erp.finances.costcenter.application.service;

import com.app.erp.finances.costcenter.domain.CostCenter;
import com.app.erp.finances.costcenter.infrastructure.mapper.CostCenterDbMapper;
import com.app.erp.finances.costcenter.infrastructure.repository.CostCenterRepositoryPort;
import com.app.erp.finances.costcenter.interfaces.rest.requests.CostCenterCreateRequest;
import com.app.erp.finances.costcenter.interfaces.rest.responses.CostCenterResponse;
import com.app.erp.inventoryrefactor.common.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CostCenterServiceImpl implements CostCenterServicePort {

    private final CostCenterRepositoryPort repository;

    public CostCenterServiceImpl(CostCenterRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public int create(int companyId, int userCreated, CostCenterCreateRequest req) {
        CostCenter domain = new CostCenter(
                0,
                req.code(),
                req.name(),
                req.description(),
                true,
                null,
                null,
                null,
                null,
                req.areaId(),
                companyId
        );

        return repository.create(companyId, userCreated, domain);
    }

    @Override
    public CostCenterResponse getById(int companyId, int costCenterId) {
        CostCenter cc = repository.findById(companyId, costCenterId);
        return CostCenterDbMapper.toResponse(cc);
    }

    @Override
    public PageResult<CostCenterResponse> search(int companyId, Integer areaId, Integer branchId, String search, int page, int pageSize) {
        PageResult<CostCenter> result = repository.search(companyId, areaId, branchId, search, page, pageSize);
        List<CostCenterResponse> items = result.items().stream()
            .map(CostCenterDbMapper::toResponse)
            .collect(Collectors.toList());
        return new PageResult<>(result.totalCount(), items);
    }
}
