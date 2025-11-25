package com.app.erp.finances.invoice.infrastructure.mapper;

import com.app.erp.finances.invoice.domain.Invoice;
import com.app.erp.finances.invoice.domain.InvoiceItem;
import com.app.erp.finances.invoice.interfaces.rest.responses.InvoiceResponse;
import com.app.erp.finances.invoice.interfaces.rest.responses.InvoiceItemResponse;
import com.app.erp.finances.invoice.interfaces.rest.responses.InvoiceSearchResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceDbMapper {

    private static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        for (int i = 1; i <= cols; i++) {
            String label = md.getColumnLabel(i);
            String name = md.getColumnName(i);
            if (columnName.equalsIgnoreCase(label) || columnName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static Invoice toDomain(ResultSet rs) throws SQLException {
        int invoiceId = rs.getInt("InvoiceID");
        int companyId = rs.getInt("CompanyID");
        int documentTypeId = rs.getInt("DocumentTypeID");
        String ruc = rs.getString("RUC");
        String serie = rs.getString("Serie");
        String numeroFactura = rs.getString("NumeroFactura");
        java.math.BigDecimal igv = rs.getBigDecimal("IGV");
        java.math.BigDecimal total = rs.getBigDecimal("Total");
        
        java.sql.Date fechaSql = rs.getDate("Fecha");
        java.time.LocalDate fecha = fechaSql != null ? fechaSql.toLocalDate() : null;
        
        String urlDocumento = hasColumn(rs, "URLDocumento") ? rs.getString("URLDocumento") : null;
        Integer purchaseOrderId = hasColumn(rs, "PurchaseOrderID") ? rs.getObject("PurchaseOrderID", Integer.class) : null;
        Integer costCenterId = hasColumn(rs, "CostCenterID") ? rs.getObject("CostCenterID", Integer.class) : null;
        
        java.time.LocalDateTime dateCreated = null;
        if (hasColumn(rs, "DateCreated") && rs.getTimestamp("DateCreated") != null) {
            dateCreated = rs.getTimestamp("DateCreated").toLocalDateTime();
        }
        
        int userCreated = hasColumn(rs, "UserCreated") ? rs.getInt("UserCreated") : 0;
        
        java.time.LocalDateTime dateModified = null;
        if (hasColumn(rs, "DateModified") && rs.getTimestamp("DateModified") != null) {
            dateModified = rs.getTimestamp("DateModified").toLocalDateTime();
        }
        
        Integer userModified = hasColumn(rs, "UserModified") ? rs.getObject("UserModified", Integer.class) : null;
        int branchId = rs.getInt("BranchID");
        String code = rs.getString("Code");
        String documentTypeName = hasColumn(rs, "DocumentTypeName") ? rs.getString("DocumentTypeName") : null;
        String costCenterName = hasColumn(rs, "CostCenterName") ? rs.getString("CostCenterName") : null;

        return new Invoice(
            invoiceId,
            companyId,
            documentTypeId,
            ruc,
            serie,
            numeroFactura,
            igv,
            total,
            fecha,
            urlDocumento,
            purchaseOrderId,
            costCenterId,
            dateCreated,
            userCreated,
            dateModified,
            userModified,
            branchId,
            code,
            documentTypeName,
            costCenterName
        );
    }

    public static InvoiceItem toItemDomain(ResultSet rs) throws SQLException {
        return new InvoiceItem(
            rs.getInt("InvoiceItemID"),
            rs.getInt("InvoiceID"),
            rs.getInt("ProductID"),
            rs.getBigDecimal("Cantidad"),
            rs.getBigDecimal("UnitCost")
        );
    }

    public static InvoiceResponse toResponse(Invoice invoice, List<InvoiceItem> items) {
        List<InvoiceItemResponse> itemResponses = items.stream()
            .map(item -> new InvoiceItemResponse(
                item.invoiceItemId(),
                item.productId(),
                item.cantidad(),
                item.unitCost()
            ))
            .collect(Collectors.toList());

        return new InvoiceResponse(
            invoice.invoiceId(),
            invoice.companyId(),
            invoice.documentTypeId(),
            invoice.documentTypeName(),
            invoice.ruc(),
            invoice.serie(),
            invoice.numeroFactura(),
            invoice.igv(),
            invoice.total(),
            invoice.fecha(),
            invoice.urlDocumento(),
            invoice.purchaseOrderId(),
            invoice.costCenterId(),
            invoice.costCenterName(),
            invoice.branchId(),
            invoice.code(),
            invoice.dateCreated(),
            invoice.userCreated(),
            itemResponses
        );
    }

    public static InvoiceSearchResponse toSearchResponse(ResultSet rs) throws SQLException {
        int invoiceId = rs.getInt("InvoiceID");
        int companyId = rs.getInt("CompanyID");
        int branchId = rs.getInt("BranchID");
        int documentTypeId = rs.getInt("DocumentTypeID");
        String documentTypeName = rs.getString("DocumentTypeName");
        String ruc = rs.getString("RUC");
        String serie = rs.getString("Serie");
        String numeroFactura = rs.getString("NumeroFactura");
        String code = rs.getString("Code");
        java.math.BigDecimal igv = rs.getBigDecimal("IGV");
        java.math.BigDecimal total = rs.getBigDecimal("Total");
        
        java.sql.Date fechaSql = rs.getDate("Fecha");
        java.time.LocalDate fecha = fechaSql != null ? fechaSql.toLocalDate() : null;
        
        String urlDocumento = hasColumn(rs, "URLDocumento") ? rs.getString("URLDocumento") : null;
        Integer purchaseOrderId = hasColumn(rs, "PurchaseOrderID") ? rs.getObject("PurchaseOrderID", Integer.class) : null;
        Integer costCenterId = hasColumn(rs, "CostCenterID") ? rs.getObject("CostCenterID", Integer.class) : null;
        String costCenterName = hasColumn(rs, "CostCenterName") ? rs.getString("CostCenterName") : null;
        
        java.time.LocalDateTime dateCreated = null;
        if (hasColumn(rs, "DateCreated") && rs.getTimestamp("DateCreated") != null) {
            dateCreated = rs.getTimestamp("DateCreated").toLocalDateTime();
        }
        
        int userCreated = hasColumn(rs, "UserCreated") ? rs.getInt("UserCreated") : 0;
        
        java.time.LocalDateTime dateModified = null;
        if (hasColumn(rs, "DateModified") && rs.getTimestamp("DateModified") != null) {
            dateModified = rs.getTimestamp("DateModified").toLocalDateTime();
        }
        
        Integer userModified = hasColumn(rs, "UserModified") ? rs.getObject("UserModified", Integer.class) : null;
        int totalRows = hasColumn(rs, "TotalRows") ? rs.getInt("TotalRows") : 0;

        return new InvoiceSearchResponse(
            invoiceId,
            companyId,
            branchId,
            documentTypeId,
            documentTypeName,
            ruc,
            serie,
            numeroFactura,
            code,
            igv,
            total,
            fecha,
            urlDocumento,
            purchaseOrderId,
            costCenterId,
            costCenterName,
            dateCreated,
            userCreated,
            dateModified,
            userModified,
            totalRows
        );
    }
}

