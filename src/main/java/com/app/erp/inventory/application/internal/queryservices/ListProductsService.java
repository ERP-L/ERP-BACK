package com.app.erp.inventory.application.internal.queryservices;

import com.app.erp.inventory.application.internal.messages.results.ProductResult;
import com.app.erp.inventory.application.internal.port.ProductReadPort;
import com.app.erp.inventory.interfaces.rest.contracts.ProductResponse;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.exceptions.AuthorizationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ListProductsService {

    private final ProductReadPort readPort;

    public ListProductsService(ProductReadPort readPort) {
        this.readPort = Objects.requireNonNull(readPort);
    }

    public List<ProductResponse> handle(AuthContext auth) {
        if (auth == null) throw new AuthorizationException("No autorizado (sin contexto)");
        if (auth.companyId() == null) throw new AuthorizationException("Token sin companyId (cid)");

        List<ProductResult> rows = readPort.getAllByCompany(auth.companyId());
        return rows.stream().map(r -> {
            ProductResponse dto = new ProductResponse();
            dto.setProductId(r.getProductId());
            dto.setSku(r.getSku());
            dto.setProductName(r.getProductName());
            dto.setCategoryId(r.getCategoryId());
            dto.setUomId(r.getUomId());
            dto.setIsSerialized(r.getIsSerialized());
            dto.setIsBatchControlled(r.getIsBatchControlled());
            dto.setReorderLevel(r.getReorderLevel());
            dto.setLeadTimeDays(r.getLeadTimeDays());
            dto.setWeight(r.getWeight());
            dto.setVolume(r.getVolume());
            dto.setStatus(r.getStatus());
            dto.setCreatedUtc(r.getCreatedUtc() == null ? null : r.getCreatedUtc().toString());
            dto.setUpdatedUtc(r.getUpdatedUtc() == null ? null : r.getUpdatedUtc().toString());
            dto.setCompanyId(r.getCompanyId());
            return dto;
        }).collect(Collectors.toList());
    }
}
