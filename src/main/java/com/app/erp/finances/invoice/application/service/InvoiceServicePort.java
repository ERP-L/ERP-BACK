package com.app.erp.finances.invoice.application.service;

import com.app.erp.finances.invoice.interfaces.rest.requests.InvoiceCreateRequest;
import com.app.erp.finances.invoice.interfaces.rest.responses.InvoiceResponse;
import com.app.erp.finances.invoice.interfaces.rest.responses.InvoiceSearchResponse;

import java.util.List;

public interface InvoiceServicePort {
    int createInvoice(int companyId, int userId, InvoiceCreateRequest request);
    InvoiceResponse findInvoiceById(int companyId, int invoiceId);
    List<InvoiceSearchResponse> searchInvoices(
        int companyId,
        Integer branchId,
        Integer costCenterId,
        Integer documentTypeId,
        String searchTerm,
        int pageNumber,
        int pageSize
    );
}
