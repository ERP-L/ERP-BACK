package com.app.erp.inventory.infrastructure.repository.sp;

import com.app.erp.inventory.application.dtos.results.InventoryBatchResult;
import com.app.erp.inventory.application.dtos.results.InventorySerialResult;
import com.app.erp.inventory.application.dtos.results.ProductDetailsResult;
import com.app.erp.inventory.application.dtos.results.InventoryProductResult;
import com.app.erp.inventory.application.port.InventoryReadPort;
import com.app.erp.inventory.infrastructure.error.DbErrorTranslator;
import com.app.erp.shared.security.AuthContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.sql.Date;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import com.app.erp.inventory.application.dtos.results.RecentMovementResult;

@Primary
@Repository
public class InventoryReadRepositorySp implements InventoryReadPort {

    private final JdbcTemplate jdbc;

    public InventoryReadRepositorySp(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<RecentMovementResult> getRecentMovements(Integer warehouseId,
                                                         String search,
                                                         LocalDate dateFrom,
                                                         LocalDate dateTo,
                                                         String type,
                                                         int page,
                                                         int size,
                                                         AuthContext auth) {
        if (auth == null || auth.companyId() == null) throw new IllegalArgumentException("missing auth/company");
        int companyId = auth.companyId();

        List<RecentMovementResult> out = new ArrayList<>();

        try {
            jdbc.execute((Connection con) -> {
                try (CallableStatement cs = con.prepareCall("{call inventory.usp_Inventory_GetRecentMovements(?,?,?,?,?,?,?,?)}")) {
                    cs.setInt(1, companyId);
                    if (warehouseId == null) cs.setNull(2, Types.INTEGER); else cs.setInt(2, warehouseId);
                    if (search == null || search.isEmpty()) cs.setNull(3, Types.NVARCHAR); else cs.setString(3, search);
                    if (dateFrom == null) cs.setNull(4, Types.DATE); else cs.setDate(4, Date.valueOf(dateFrom));
                    if (dateTo == null) cs.setNull(5, Types.DATE); else cs.setDate(5, Date.valueOf(dateTo));
                    if (type == null || type.isEmpty()) cs.setNull(6, Types.NVARCHAR); else cs.setString(6, type);
                    cs.setInt(7, Math.max(1, page));
                    cs.setInt(8, Math.max(1, size));

                    boolean has = cs.execute();
                    if (has) {
                        try (ResultSet rs = cs.getResultSet()) {
                            while (rs.next()) {
                                RecentMovementResult r = new RecentMovementResult();
                                r.setTipo(rs.getString("Tipo"));
                                r.setProducto(rs.getString("Producto"));
                                r.setCantidad(rs.getBigDecimal("Cantidad"));
                                Date d = rs.getDate("Fecha");
                                r.setFecha(d == null ? null : d.toLocalDate());
                                r.setUsuario(rs.getString("Usuario"));
                                r.setReferencia(rs.getString("Referencia"));
                                out.add(r);
                            }
                        }
                    }
                }
                return null;
            });
        } catch (Exception ex) {
            // inspect cause chain for SQLException
            Throwable cause = ex;
            while (cause != null) {
                if (cause instanceof SQLException) {
                    throw DbErrorTranslator.translate((SQLException) cause);
                }
                cause = cause.getCause();
            }
            if (ex instanceof RuntimeException) throw (RuntimeException) ex;
            throw new RuntimeException(ex);
        }

        return out;
    }

    @Override
    public ProductDetailsResult getProductDetailsInWarehouse(int warehouseId, int productId,
                                                             String orderBatch, int pageBatch, int sizeBatch,
                                                             String orderSerial, int pageSerial, int sizeSerial,
                                                             AuthContext auth) {
        if (auth == null || auth.companyId() == null) throw new IllegalArgumentException("missing auth/company");
        int companyId = auth.companyId();

        try {
            // First, call the SP and collect resultsets for batches/serials. The SP does its own guards.
            List<InventoryBatchResult> batches = new ArrayList<>();
            List<InventorySerialResult> serials = new ArrayList<>();

            jdbc.execute((Connection con) -> {
                String call = "{call inventory.usp_Inventory_GetProductDetailsInWarehouse(?,?,?,?,?,?,?,?,?,?)}";
                // Note: SP signature in your script had 9 params; keep flexible: we'll use 9 params as declared
                try (CallableStatement cs = con.prepareCall("{call inventory.usp_Inventory_GetProductDetailsInWarehouse(?,?,?,?,?,?,?,?,?)}")) {
                    cs.setInt(1, companyId);
                    cs.setInt(2, warehouseId);
                    cs.setInt(3, productId);
                    cs.setString(4, orderBatch == null ? "ExpirationDate" : orderBatch);
                    cs.setInt(5, Math.max(1, pageBatch));
                    cs.setInt(6, Math.max(1, sizeBatch));
                    cs.setString(7, orderSerial == null ? "SerialNumber" : orderSerial);
                    cs.setInt(8, Math.max(1, pageSerial));
                    cs.setInt(9, Math.max(1, sizeSerial));

                    boolean has = cs.execute();
                    while (has) {
                        try (ResultSet rs = cs.getResultSet()) {
                            if (rs == null) { has = cs.getMoreResults(); continue; }
                            ResultSetMetaData md = rs.getMetaData();
                            String first = md.getColumnLabel(1);
                            if (first.equalsIgnoreCase("BatchID") || first.equalsIgnoreCase("BatchNumber")) {
                                while (rs.next()) {
                                    InventoryBatchResult b = new InventoryBatchResult();
                                    b.setBatchId(rs.getObject("BatchID") == null ? null : rs.getInt("BatchID"));
                                    b.setBatchNumber(rs.getString("BatchNumber"));
                                    b.setManufactureDate(rs.getObject("ManufactureDate") == null ? null : rs.getDate("ManufactureDate").toLocalDate());
                                    b.setExpirationDate(rs.getObject("ExpirationDate") == null ? null : rs.getDate("ExpirationDate").toLocalDate());
                                    b.setCreatedUtc(rs.getObject("CreatedUtc") == null ? null : rs.getTimestamp("CreatedUtc").toInstant().atOffset(OffsetDateTime.now().getOffset()));
                                    b.setQuantity(rs.getBigDecimal("Quantity"));
                                    b.setReserved(rs.getBigDecimal("Reserved"));
                                    b.setLastLocation(rs.getString("LastLocation"));
                                    b.setUpdatedUtc(rs.getObject("UpdatedUtc") == null ? null : rs.getTimestamp("UpdatedUtc").toInstant().atOffset(OffsetDateTime.now().getOffset()));
                                    batches.add(b);
                                }
                            } else if (first.equalsIgnoreCase("SerialID") || first.equalsIgnoreCase("SerialNumber")) {
                                while (rs.next()) {
                                    InventorySerialResult s = new InventorySerialResult();
                                    s.setSerialId(rs.getObject("SerialID") == null ? null : rs.getInt("SerialID"));
                                    s.setSerialNumber(rs.getString("SerialNumber"));
                                    s.setUnitCost(rs.getBigDecimal("UnitCost"));
                                    s.setCreatedUtc(rs.getObject("CreatedUtc") == null ? null : rs.getTimestamp("CreatedUtc").toInstant().atOffset(OffsetDateTime.now().getOffset()));
                                    s.setLastLocation(rs.getString("LastLocation"));
                                    s.setUpdatedUtc(rs.getObject("UpdatedUtc") == null ? null : rs.getTimestamp("UpdatedUtc").toInstant().atOffset(OffsetDateTime.now().getOffset()));
                                    serials.add(s);
                                }
                            } else {
                                // unknown resultset: ignore
                            }
                        }
                        has = cs.getMoreResults();
                    }
                }
                return null;
            });

            // For product base info reuse existing query to get InventoryProductResult (lightweight)
            InventoryProductResult product = jdbc.queryForObject(
                    "SELECT p.ProductID, p.SKU, p.ProductName, p.CategoryID, p.UOMID, p.IsSerialized, p.IsBatchControlled, p.Status, p.CreatedUtc, p.UpdatedUtc, p.CompanyID, " +
                            "CASE WHEN p.IsSerialized = 1 THEN 'SERIAL' WHEN p.IsBatchControlled = 1 THEN 'BATCH' ELSE 'NORMAL' END AS TrackingMode, " +
                            "CASE WHEN p.IsSerialized = 1 THEN 'Serie' WHEN p.IsBatchControlled = 1 THEN 'Lote' ELSE 'Sin etiqueta' END AS TrackingLabel " +
                            "FROM inventory.Product p WHERE p.ProductID = ? AND p.CompanyID = ?",
                    (rs, rn) -> {
                        InventoryProductResult r = new InventoryProductResult();
                        r.setProductId(rs.getInt("ProductID"));
                        r.setSku(rs.getString("SKU"));
                        r.setProductName(rs.getString("ProductName"));
                        r.setCategoryId(rs.getObject("CategoryID") == null ? null : rs.getInt("CategoryID"));
                        r.setUomId(rs.getObject("UOMID") == null ? null : rs.getInt("UOMID"));
                        r.setIsSerialized(rs.getBoolean("IsSerialized"));
                        r.setIsBatchControlled(rs.getBoolean("IsBatchControlled"));
                        r.setStatus(rs.getObject("Status") == null ? null : rs.getInt("Status"));
                        r.setCreatedUtc(rs.getObject("CreatedUtc") == null ? null : rs.getTimestamp("CreatedUtc").toInstant().atOffset(OffsetDateTime.now().getOffset()));
                        r.setUpdatedUtc(rs.getObject("UpdatedUtc") == null ? null : rs.getTimestamp("UpdatedUtc").toInstant().atOffset(OffsetDateTime.now().getOffset()));
                        r.setCompanyId(rs.getInt("CompanyID"));
                        r.setTrackingMode(rs.getString("TrackingMode"));
                        r.setTrackingLabel(rs.getString("TrackingLabel"));
                        return r;
                    },
                    productId, companyId
            );

            ProductDetailsResult out = new ProductDetailsResult();
            out.setProduct(product);
            out.setBatches(batches.isEmpty() ? null : batches);
            out.setSerials(serials.isEmpty() ? null : serials);
            return out;
        } catch (Exception ex) {
            // JdbcTemplate typically throws runtime DataAccessExceptions which may wrap SQLExceptions.
            // Inspect the thrown exception and its cause chain for a SQLException to let DbErrorTranslator handle DB errors.
            if (ex instanceof RuntimeException) {
                Throwable cause = ex.getCause();
                if (cause instanceof SQLException) {
                    throw DbErrorTranslator.translate((SQLException) cause);
                }
                // propagate runtime exceptions as-is
                throw (RuntimeException) ex;
            }

            // Checked exceptions: see if cause is a SQLException
            Throwable cause = ex.getCause();
            if (cause instanceof SQLException) {
                throw DbErrorTranslator.translate((SQLException) cause);
            }
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Integer getCategoryCompanyId(int categoryId) {
        try {
            return jdbc.query(
                    "SELECT CompanyID FROM inventory.ProductCategory WHERE CategoryID = ?",
                    ps -> ps.setInt(1, categoryId),
                    rs -> rs.next() ? rs.getInt("CompanyID") : null
            );
        } catch (RuntimeException ex) {
            // Let caller handle translated DB/runtime exceptions consistently
            throw ex;
        }
    }
}
