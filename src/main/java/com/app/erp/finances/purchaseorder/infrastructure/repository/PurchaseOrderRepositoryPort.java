package com.app.erp.finances.purchaseorder.infrastructure.repository;

import com.app.erp.finances.purchaseorder.domain.PurchaseOrder;
import com.app.erp.finances.purchaseorder.domain.PurchaseOrderProduct;
import com.app.erp.finances.purchaseorder.interfaces.rest.responses.PurchaseOrderSearchResponse;
import com.app.erp.shared.dtos.PagedResponse;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepositoryPort {
    int create(PurchaseOrder purchaseOrder, List<PurchaseOrderProduct> products);
    Optional<PurchaseOrder> findById(int id);
    List<PurchaseOrderProduct> findProductsByPurchaseOrderId(int purchaseOrderId);
    PagedResponse<PurchaseOrderSearchResponse> search(int companyId, Integer areaId, Integer costCenterId, String purchaseTypeCode, String statusCode, String searchTerm, int pageNumber, int pageSize);
}
