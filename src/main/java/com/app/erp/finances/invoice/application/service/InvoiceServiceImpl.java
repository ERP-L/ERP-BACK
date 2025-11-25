package com.app.erp.finances.invoice.application.service;

import com.app.erp.finances.invoice.domain.Invoice;
import com.app.erp.finances.invoice.domain.InvoiceItem;
import com.app.erp.finances.invoice.infrastructure.mapper.InvoiceDbMapper;
import com.app.erp.finances.invoice.infrastructure.repository.InvoiceRepositoryPort;
import com.app.erp.finances.invoice.interfaces.rest.requests.InvoiceCreateRequest;
import com.app.erp.finances.invoice.interfaces.rest.responses.InvoiceResponse;
import com.app.erp.finances.invoice.interfaces.rest.responses.InvoiceSearchResponse;
import com.app.erp.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceServicePort {

    private final InvoiceRepositoryPort repository;

    @Override
    public int createInvoice(int companyId, int userId, InvoiceCreateRequest request) {
        Invoice newInvoice = new Invoice(
            0,
            companyId,
            request.documentTypeId(),
            request.ruc(),
            request.serie(),
            request.numeroFactura(),
            request.igv(),
            request.total(),
            request.fecha(),
            request.urlDocumento(),
            request.purchaseOrderId(),
            request.costCenterId(),
            LocalDateTime.now(),
            userId,
            null,
            null,
            request.branchId(),
            request.code(),
            null,
            null
        );

        List<InvoiceItem> items = request.items().stream()
            .map(item -> new InvoiceItem(0, 0, item.productId(), item.cantidad(), item.unitCost()))
            .collect(Collectors.toList());

        return repository.create(newInvoice, items);
    }

    @Override
    public InvoiceResponse findInvoiceById(int companyId, int invoiceId) {
        Invoice invoice = repository.findById(companyId, invoiceId)
            .orElseThrow(() -> new NotFoundException("Invoice not found"));

        List<InvoiceItem> items = repository.findItemsByInvoiceId(companyId, invoiceId);

        return InvoiceDbMapper.toResponse(invoice, items);
    }

    @Override
    public List<InvoiceSearchResponse> searchInvoices(
        int companyId,
        Integer branchId,
        Integer costCenterId,
        Integer documentTypeId,
        String searchTerm,
        int pageNumber,
        int pageSize
    ) {
        return repository.search(companyId, branchId, costCenterId, documentTypeId, searchTerm, pageNumber, pageSize);
    }
}
