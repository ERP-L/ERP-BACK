package com.app.erp.finances.invoice.infrastructure.repository;

import com.app.erp.finances.invoice.domain.Invoice;
import com.app.erp.finances.invoice.domain.InvoiceItem;
import com.app.erp.finances.invoice.interfaces.rest.responses.InvoiceSearchResponse;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepositoryPort {
    int create(Invoice invoice, List<InvoiceItem> items);
    Optional<Invoice> findById(int companyId, int invoiceId);
    List<InvoiceItem> findItemsByInvoiceId(int companyId, int invoiceId);
    List<InvoiceSearchResponse> search(
        int companyId,
        Integer branchId,
        Integer costCenterId,
        Integer documentTypeId,
        String searchTerm,
        int pageNumber,
        int pageSize
    );
}
