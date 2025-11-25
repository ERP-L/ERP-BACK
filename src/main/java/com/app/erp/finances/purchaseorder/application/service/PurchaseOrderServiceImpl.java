package com.app.erp.finances.purchaseorder.application.service;

import com.app.erp.finances.purchaseorder.domain.PurchaseOrder;
import com.app.erp.finances.purchaseorder.domain.PurchaseOrderProduct;
import com.app.erp.finances.purchaseorder.infrastructure.mapper.PurchaseOrderDbMapper;
import com.app.erp.finances.purchaseorder.infrastructure.repository.PurchaseOrderRepositoryPort;
import com.app.erp.finances.purchaseorder.interfaces.rest.requests.PurchaseOrderCreateRequest;
import com.app.erp.finances.purchaseorder.interfaces.rest.responses.PurchaseOrderResponse;
import com.app.erp.finances.purchaseorder.interfaces.rest.responses.PurchaseOrderSearchResponse;
import com.app.erp.shared.dtos.PagedResponse;
import com.app.erp.shared.exceptions.AuthorizationException;
import com.app.erp.shared.exceptions.NotFoundException;
import com.app.erp.shared.security.AuthContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderServicePort {

    private final PurchaseOrderRepositoryPort purchaseOrderRepository;

    @Override
    public int createPurchaseOrder(int companyId, int userId, PurchaseOrderCreateRequest request) {
        PurchaseOrder newPurchaseOrder = new PurchaseOrder(
                0,
                companyId,
                request.costCenterId(),
                request.purchaseTypeId(),
                request.codigo(),
                request.ruc(),
                request.nombre(),
                request.direccion(),
                request.telefono(),
                request.fechaEmision(),
                BigDecimal.ZERO,
                0, // StatusID will be set by the SP
                userId,
                LocalDateTime.now(),
                null,
                null,
                null,
                null,
                null
        );

        List<PurchaseOrderProduct> products = request.products().stream()
                .map(p -> new PurchaseOrderProduct(0, 0, p.productId(), p.warehouseId(), p.cantidad(), p.unitCost()))
                .collect(Collectors.toList());

        return purchaseOrderRepository.create(newPurchaseOrder, products);
    }

    @Override
    public PurchaseOrderResponse findPurchaseOrderById(int companyId, int id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Purchase order not found"));

        if (purchaseOrder.companyId() != companyId) {
            throw new AuthorizationException("You are not authorized to view this purchase order");
        }

        List<PurchaseOrderProduct> products = purchaseOrderRepository.findProductsByPurchaseOrderId(id);

        return PurchaseOrderDbMapper.toResponse(purchaseOrder, products);
    }

    @Override
    public PagedResponse<PurchaseOrderSearchResponse> searchPurchaseOrders(int companyId, Integer areaId, Integer costCenterId, String purchaseTypeCode, String statusCode, String searchTerm, int pageNumber, int pageSize) {
        return purchaseOrderRepository.search(companyId, areaId, costCenterId, purchaseTypeCode, statusCode, searchTerm, pageNumber, pageSize);
    }
}
