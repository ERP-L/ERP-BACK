package com.app.erp.finances.purchaseorder.application.service;

import com.app.erp.finances.purchaseorder.interfaces.rest.requests.PurchaseOrderCreateRequest;
import com.app.erp.finances.purchaseorder.interfaces.rest.responses.PurchaseOrderResponse;
import com.app.erp.finances.purchaseorder.interfaces.rest.responses.PurchaseOrderSearchResponse;
import com.app.erp.shared.dtos.PagedResponse;
import com.app.erp.shared.security.AuthContext;

public interface PurchaseOrderServicePort {
    int createPurchaseOrder(int companyId, int userId, PurchaseOrderCreateRequest request);
    PurchaseOrderResponse findPurchaseOrderById(int companyId, int id);
    PagedResponse<PurchaseOrderSearchResponse> searchPurchaseOrders(int companyId, Integer areaId, Integer costCenterId, String purchaseTypeCode, String statusCode, String searchTerm, int pageNumber, int pageSize);
}
