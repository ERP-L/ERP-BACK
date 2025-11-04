package com.app.erp.inventory.application.usecase;

import com.app.erp.inventory.application.dtos.results.InventoryProductResult;
import com.app.erp.inventory.application.port.InventoryWarehouseReadPort;
import com.app.erp.inventory.interfaces.rest.contracts.InventoryProductResponse;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.shared.exceptions.AuthorizationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ListProductsInWarehouseHandler {

    private final InventoryWarehouseReadPort readPort;

    public ListProductsInWarehouseHandler(InventoryWarehouseReadPort readPort) {
        this.readPort = Objects.requireNonNull(readPort);
    }

    public List<InventoryProductResponse> handle(AuthContext auth,
                                                 Integer warehouseId,
                                                 Integer productId,
                                                 Integer categoryId,
                                                 String search,
                                                 String orderBy,
                                                 Integer pageNumber,
                                                 Integer pageSize) {
        if (auth == null) throw new AuthorizationException("No autorizado (sin contexto)");
        if (auth.companyId() == null) throw new AuthorizationException("Token sin companyId (cid)");

        int cid = auth.companyId();
        int wid = warehouseId == null ? 0 : warehouseId;
        int pg = pageNumber == null ? 1 : pageNumber;
        int ps = pageSize == null ? 50 : pageSize;

        List<InventoryProductResult> rows = readPort.listProductsInWarehouse(cid, wid, productId, categoryId, search, orderBy, pg, ps);

        return rows.stream().map(r -> {
            InventoryProductResponse dto = new InventoryProductResponse();
            dto.setProductId(r.getProductId());
            dto.setSku(r.getSku());
            dto.setProductName(r.getProductName());
            dto.setCategoryId(r.getCategoryId());
            dto.setUomId(r.getUomId());
            dto.setIsSerialized(r.getIsSerialized());
            dto.setIsBatchControlled(r.getIsBatchControlled());
            dto.setStatus(r.getStatus());
            dto.setCreatedUtc(r.getCreatedUtc() == null ? null : r.getCreatedUtc().toString());
            dto.setCompanyId(r.getCompanyId());
            dto.setTrackingMode(r.getTrackingMode());
            dto.setTrackingLabel(r.getTrackingLabel());
            dto.setAvgCost(r.getAvgCost());
            dto.setQuantity(r.getQuantity());
            dto.setReserved(r.getReserved());
            dto.setLocationsStr(r.getLocationsStr());
            dto.setUpdatedUtc(r.getUpdatedUtc() == null ? null : r.getUpdatedUtc().toString());
            return dto;
        }).collect(Collectors.toList());
    }
}
