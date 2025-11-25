package com.app.erp.finances.invoice.infrastructure.repository.sp;

import com.app.erp.finances.invoice.domain.Invoice;
import com.app.erp.finances.invoice.domain.InvoiceItem;
import com.app.erp.finances.invoice.infrastructure.mapper.InvoiceDbMapper;
import com.app.erp.finances.invoice.infrastructure.repository.InvoiceRepositoryPort;
import com.app.erp.finances.invoice.interfaces.rest.responses.InvoiceSearchResponse;
import com.app.erp.shared.infrastructure.error.DbErrorTranslator;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class InvoiceRepositorySp implements InvoiceRepositoryPort {

    private final DataSource dataSource;

    private static class SpCreate {
        static final String CALL = "{ call finance.usp_Invoice_Create(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_DOCUMENT_TYPE_ID = 2;
        static final int IDX_RUC = 3;
        static final int IDX_SERIE = 4;
        static final int IDX_NUMERO_FACTURA = 5;
        static final int IDX_IGV = 6;
        static final int IDX_TOTAL = 7;
        static final int IDX_FECHA = 8;
        static final int IDX_URL_DOCUMENTO = 9;
        static final int IDX_PURCHASE_ORDER_ID = 10;
        static final int IDX_COST_CENTER_ID = 11;
        static final int IDX_USER_CREATED = 12;
        static final int IDX_BRANCH_ID = 13;
        static final int IDX_CODE = 14;
        static final int IDX_ITEMS = 15;
    }

    private static class SpGetById {
        static final String CALL = "{ call finance.usp_Invoice_GetById(?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_INVOICE_ID = 2;
    }

    private static class SpGetItems {
        static final String CALL = "{ call finance.usp_Invoice_GetItems(?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_INVOICE_ID = 2;
    }

    private static class SpSearch {
        static final String CALL = "{ call finance.usp_Invoice_Search(?, ?, ?, ?, ?, ?, ?) }";
        static final int IDX_COMPANY_ID = 1;
        static final int IDX_BRANCH_ID = 2;
        static final int IDX_COST_CENTER_ID = 3;
        static final int IDX_DOCUMENT_TYPE_ID = 4;
        static final int IDX_SEARCH_TERM = 5;
        static final int IDX_PAGE_NUMBER = 6;
        static final int IDX_PAGE_SIZE = 7;
    }

    public InvoiceRepositorySp(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Override
    public int create(Invoice invoice, List<InvoiceItem> items) {
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(SpCreate.CALL)) {

            SQLServerDataTable itemsTable = new SQLServerDataTable();
            itemsTable.addColumnMetadata("ProductID", java.sql.Types.INTEGER);
            itemsTable.addColumnMetadata("Cantidad", java.sql.Types.DECIMAL);
            itemsTable.addColumnMetadata("UnitCost", java.sql.Types.DECIMAL);

            for (InvoiceItem item : items) {
                itemsTable.addRow(item.productId(), item.cantidad(), item.unitCost());
            }

            cs.setInt(SpCreate.IDX_COMPANY_ID, invoice.companyId());
            cs.setInt(SpCreate.IDX_DOCUMENT_TYPE_ID, invoice.documentTypeId());
            cs.setString(SpCreate.IDX_RUC, invoice.ruc());
            cs.setString(SpCreate.IDX_SERIE, invoice.serie());
            cs.setString(SpCreate.IDX_NUMERO_FACTURA, invoice.numeroFactura());
            cs.setBigDecimal(SpCreate.IDX_IGV, invoice.igv());
            cs.setBigDecimal(SpCreate.IDX_TOTAL, invoice.total());
            cs.setDate(SpCreate.IDX_FECHA, java.sql.Date.valueOf(invoice.fecha()));
            cs.setString(SpCreate.IDX_URL_DOCUMENTO, invoice.urlDocumento());
            
            if (invoice.purchaseOrderId() != null) {
                cs.setInt(SpCreate.IDX_PURCHASE_ORDER_ID, invoice.purchaseOrderId());
            } else {
                cs.setNull(SpCreate.IDX_PURCHASE_ORDER_ID, java.sql.Types.INTEGER);
            }
            
            if (invoice.costCenterId() != null) {
                cs.setInt(SpCreate.IDX_COST_CENTER_ID, invoice.costCenterId());
            } else {
                cs.setNull(SpCreate.IDX_COST_CENTER_ID, java.sql.Types.INTEGER);
            }
            
            cs.setInt(SpCreate.IDX_USER_CREATED, invoice.userCreated());
            cs.setInt(SpCreate.IDX_BRANCH_ID, invoice.branchId());
            cs.setString(SpCreate.IDX_CODE, invoice.code());
            cs.setObject(SpCreate.IDX_ITEMS, itemsTable);

            cs.execute();

            ResultSet rs = cs.getResultSet();
            while (rs == null && (cs.getUpdateCount() != -1 || cs.getMoreResults())) {
                rs = cs.getResultSet();
            }
            
            if (rs != null && rs.next()) {
                return rs.getInt("InvoiceID");
            }
            
            throw new RuntimeException("No se pudo obtener el ID de la factura creada");

        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        } catch (DataAccessException dae) {
            Throwable root = dae.getRootCause();
            if (root instanceof SQLException) {
                throw DbErrorTranslator.translate((SQLException) root);
            }
            throw dae;
        }
    }

    @Override
    public Optional<Invoice> findById(int companyId, int invoiceId) {
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(SpGetById.CALL)) {

            cs.setInt(SpGetById.IDX_COMPANY_ID, companyId);
            cs.setInt(SpGetById.IDX_INVOICE_ID, invoiceId);
            
            boolean has = cs.execute();
            ResultSet rs = cs.getResultSet();
            while (rs == null && (cs.getUpdateCount() != -1 || cs.getMoreResults())) {
                rs = cs.getResultSet();
            }
            
            if (rs != null && rs.next()) {
                Invoice invoice = InvoiceDbMapper.toDomain(rs);
                return Optional.of(invoice);
            }
            return Optional.empty();

        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        } catch (DataAccessException dae) {
            Throwable root = dae.getRootCause();
            if (root instanceof SQLException) {
                throw DbErrorTranslator.translate((SQLException) root);
            }
            throw dae;
        }
    }

    @Override
    public List<InvoiceItem> findItemsByInvoiceId(int companyId, int invoiceId) {
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(SpGetItems.CALL)) {

            cs.setInt(SpGetItems.IDX_COMPANY_ID, companyId);
            cs.setInt(SpGetItems.IDX_INVOICE_ID, invoiceId);
            
            boolean has = cs.execute();
            ResultSet rs = cs.getResultSet();
            while (rs == null && (cs.getUpdateCount() != -1 || cs.getMoreResults())) {
                rs = cs.getResultSet();
            }
            
            List<InvoiceItem> items = new ArrayList<>();
            if (rs != null) {
                while (rs.next()) {
                    items.add(InvoiceDbMapper.toItemDomain(rs));
                }
            }
            return items;

        } catch (SQLException e) {
            throw DbErrorTranslator.translate(e);
        } catch (DataAccessException e) {
            Throwable root = e.getRootCause();
            if (root instanceof SQLException) {
                throw DbErrorTranslator.translate((SQLException) root);
            }
            throw e;
        }
    }

    @Override
    public List<InvoiceSearchResponse> search(
        int companyId,
        Integer branchId,
        Integer costCenterId,
        Integer documentTypeId,
        String searchTerm,
        int pageNumber,
        int pageSize
    ) {
        try (Connection con = dataSource.getConnection();
             CallableStatement cs = con.prepareCall(SpSearch.CALL)) {

            cs.setInt(SpSearch.IDX_COMPANY_ID, companyId);
            
            if (branchId != null) {
                cs.setInt(SpSearch.IDX_BRANCH_ID, branchId);
            } else {
                cs.setNull(SpSearch.IDX_BRANCH_ID, java.sql.Types.INTEGER);
            }
            
            if (costCenterId != null) {
                cs.setInt(SpSearch.IDX_COST_CENTER_ID, costCenterId);
            } else {
                cs.setNull(SpSearch.IDX_COST_CENTER_ID, java.sql.Types.INTEGER);
            }
            
            if (documentTypeId != null) {
                cs.setInt(SpSearch.IDX_DOCUMENT_TYPE_ID, documentTypeId);
            } else {
                cs.setNull(SpSearch.IDX_DOCUMENT_TYPE_ID, java.sql.Types.INTEGER);
            }
            
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                cs.setString(SpSearch.IDX_SEARCH_TERM, searchTerm);
            } else {
                cs.setNull(SpSearch.IDX_SEARCH_TERM, java.sql.Types.NVARCHAR);
            }
            
            cs.setInt(SpSearch.IDX_PAGE_NUMBER, pageNumber);
            cs.setInt(SpSearch.IDX_PAGE_SIZE, pageSize);

            boolean has = cs.execute();
            ResultSet rs = cs.getResultSet();
            while (rs == null && (cs.getUpdateCount() != -1 || cs.getMoreResults())) {
                rs = cs.getResultSet();
            }

            List<InvoiceSearchResponse> results = new ArrayList<>();
            if (rs != null) {
                while (rs.next()) {
                    results.add(InvoiceDbMapper.toSearchResponse(rs));
                }
            }
            return results;


        } catch (SQLException ex) {
            throw DbErrorTranslator.translate(ex);
        } catch (DataAccessException dae) {
            Throwable root = dae.getRootCause();
            if (root instanceof SQLException) {
                throw DbErrorTranslator.translate((SQLException) root);
            }
            throw dae;
        }
    }
}
