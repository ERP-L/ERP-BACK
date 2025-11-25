package com.app.erp.finances.purchaseorder.infrastructure.mapper;

import com.app.erp.finances.purchaseorder.domain.PurchaseOrder;
import com.app.erp.finances.purchaseorder.domain.PurchaseOrderProduct;
import com.app.erp.finances.purchaseorder.interfaces.rest.responses.PurchaseOrderProductResponse;
import com.app.erp.finances.purchaseorder.interfaces.rest.responses.PurchaseOrderResponse;
import com.app.erp.finances.purchaseorder.interfaces.rest.responses.PurchaseOrderSearchResponse;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.stream.Collectors;

public class PurchaseOrderDbMapper {

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

    public static PurchaseOrder toDomain(ResultSet rs) throws SQLException {
        // Leer de forma segura columnas que pueden no estar presentes en todos los SPs
        int purchaseOrderId = rs.getInt("PurchaseOrderID");
        int companyId = rs.getInt("CompanyID");
        int costCenterId = rs.getInt("CostCenterID");

        int purchaseTypeId = hasColumn(rs, "PurchaseTypeID") ? rs.getInt("PurchaseTypeID") : 0;
        String codigo = hasColumn(rs, "Codigo") ? rs.getString("Codigo") : null;
        String ruc = hasColumn(rs, "RUC") ? rs.getString("RUC") : null;
        String nombre = hasColumn(rs, "Nombre") ? rs.getString("Nombre") : null;
        String direccion = hasColumn(rs, "Direccion") ? rs.getString("Direccion") : null;
        String telefono = hasColumn(rs, "Telefono") ? rs.getString("Telefono") : null;

        java.sql.Date fechaSql = hasColumn(rs, "FechaEmision") ? rs.getDate("FechaEmision") : null;
        java.time.LocalDate fechaEmision = fechaSql != null ? fechaSql.toLocalDate() : null;

        java.math.BigDecimal total = hasColumn(rs, "Total") ? rs.getBigDecimal("Total") : null;
        int statusId = hasColumn(rs, "StatusID") ? rs.getInt("StatusID") : 0;
        int userCreated = hasColumn(rs, "UserCreated") ? rs.getInt("UserCreated") : 0;

        java.time.LocalDateTime dateCreated = null;
        if (hasColumn(rs, "DateCreated") && rs.getTimestamp("DateCreated") != null) {
            dateCreated = rs.getTimestamp("DateCreated").toLocalDateTime();
        }

        Integer userModified = hasColumn(rs, "UserModified") ? rs.getObject("UserModified", Integer.class) : null;
        java.time.LocalDateTime dateModified = null;
        if (hasColumn(rs, "DateModified") && rs.getTimestamp("DateModified") != null) {
            dateModified = rs.getTimestamp("DateModified").toLocalDateTime();
        }

        String costCenterName = hasColumn(rs, "CostCenterName") ? rs.getString("CostCenterName") : null;
        String purchaseType = hasColumn(rs, "PurchaseType") ? rs.getString("PurchaseType") : null;
        String status = hasColumn(rs, "Status") ? rs.getString("Status") : null;

        return new PurchaseOrder(
                purchaseOrderId,
                companyId,
                costCenterId,
                purchaseTypeId,
                codigo,
                ruc,
                nombre,
                direccion,
                telefono,
                fechaEmision,
                total,
                statusId,
                userCreated,
                dateCreated,
                userModified,
                dateModified,
                costCenterName,
                purchaseType,
                status
        );
    }

    public static PurchaseOrderSearchResponse toSearchResponse(ResultSet rs) throws SQLException {
        Integer totalRows = null;
        Object trObj = rs.getObject("TotalRows"); // usar el nombre real devuelto por el SP
        if (trObj != null) {
            totalRows = ((Number) trObj).intValue();
        }

        java.sql.Date fecha = rs.getDate("FechaEmision");
        java.time.LocalDate fechaEmision = fecha != null ? fecha.toLocalDate() : null;

        java.sql.Timestamp ts = rs.getTimestamp("DateCreated");
        java.time.LocalDateTime dateCreated = ts != null ? ts.toLocalDateTime() : null;

        return new PurchaseOrderSearchResponse(
                rs.getInt("PurchaseOrderID"),
                rs.getString("Codigo"),
                rs.getInt("CompanyID"),
                rs.getInt("CostCenterID"),
                rs.getString("CostCenterName"),
                rs.getString("CostCenterCode"),
                rs.getString("RUC"),
                rs.getString("Nombre"),
                rs.getString("Direccion"),
                rs.getString("Telefono"),
                fechaEmision,
                rs.getBigDecimal("Total"),
                rs.getString("PurchaseType"),
                rs.getString("Status"),
                dateCreated,
                rs.getInt("UserCreated"),
                totalRows
        );
    }

    public static PurchaseOrderResponse toResponse(PurchaseOrder order, List<PurchaseOrderProduct> products) {
        List<PurchaseOrderProductResponse> productResponses = products.stream()
                .map(p -> new PurchaseOrderProductResponse(p.productId(), p.warehouseId(), p.cantidad(), p.unitCost()))
                .collect(Collectors.toList());

        return new PurchaseOrderResponse(
                order.purchaseOrderId(),
                order.codigo(),
                order.companyId(),
                order.costCenterId(),
                order.costCenterName(),
                order.ruc(),
                order.nombre(),
                order.direccion(),
                order.telefono(),
                order.fechaEmision(),
                order.total(),
                order.purchaseType(),
                order.status(),
                productResponses
        );
    }
}
