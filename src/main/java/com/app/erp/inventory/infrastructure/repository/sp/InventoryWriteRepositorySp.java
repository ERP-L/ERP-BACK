package com.app.erp.inventory.infrastructure.repository.sp;

import com.app.erp.inventory.application.dtos.commands.InventoryLineCommand;
import com.app.erp.inventory.application.dtos.commands.PostInventoryMovementCommand;
import com.app.erp.inventory.application.dtos.results.PostInventoryMovementResult;
import com.app.erp.inventory.application.port.InventoryWritePort;
import com.app.erp.shared.security.AuthContext;
import com.app.erp.inventory.infrastructure.error.DbErrorTranslator;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryWriteRepositorySp implements InventoryWritePort {

    private final JdbcTemplate jdbc;

    @Autowired
    public InventoryWriteRepositorySp(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public PostInventoryMovementResult postMovement(PostInventoryMovementCommand command, AuthContext authContext) {
        try {
            return jdbc.execute((Connection con) -> {
                // Resolve helpers first (idempotent SPs)
                List<InventoryLineCommand> resolved = new ArrayList<>();
                for (InventoryLineCommand l : command.getLines()) {
                    InventoryLineCommand copy = l; // shallow copy is fine for now
                    // Resolve batch (pass manufacture/expiration dates if present)
                    if (copy.getBatchId() == null && copy.getBatchNumber() != null && command.isAutoCreateBatch()) {
                        Integer batchId = callCreateBatch(con, copy.getProductId(), copy.getBatchNumber(),
                                copy.getBatchManufactureDate(), copy.getBatchExpirationDate(), null);
                        copy.setBatchId(batchId);
                    }
                    // Resolve serial
                    if (copy.getSerialId() == null && copy.getSerialNumber() != null && command.isAutoCreateSerial()) {
                        Integer serialId = callCreateSerial(con, copy.getProductId(), copy.getSerialNumber());
                        copy.setSerialId(serialId);
                    }
                    // Optionally create location (decorative)
                    if (command.isAutoCreateLocation() && copy.getLocationCode() != null && !copy.getLocationCode().trim().isEmpty()) {
                        Integer targetWarehouse = determineLocationWarehouse(command.getMovementType(), command.getFromWarehouseId(), command.getToWarehouseId(), copy.getQuantity());
                        callCreateItemLocation(con, targetWarehouse, copy.getProductId(), copy.getBatchId(), copy.getSerialId(), copy.getLocationCode(), null);
                    }
                    resolved.add(copy);
                }

                // Pre-validation for IN movements with InvoiceID
                if ("IN".equalsIgnoreCase(command.getMovementType()) && command.getInvoiceId() != null) {
                    resolved = preValidateInvoice(con, command.getInvoiceId(), resolved);
                }

                // Build TVP
                SQLServerDataTable tvp = buildLineTvp(resolved);

                // Select wrapper
                boolean serialMode = "SERIAL".equalsIgnoreCase(command.getLineMode());
                String movement = command.getMovementType().toUpperCase();

                int createdBy = authContext != null ? authContext.userId() : 0;
                long movementId;
                if (serialMode) {
                    movementId = callWrapperSerial(con, movement, command, tvp, createdBy);
                } else {
                    movementId = callWrapperNonSerial(con, movement, command, tvp, createdBy);
                }

                OffsetDateTime movementDate = command.getMovementDate() != null ? command.getMovementDate() : OffsetDateTime.now();
                return new PostInventoryMovementResult(movementId, movementDate, command.getInvoiceId(), command.getTemporalId());
            });
        } catch (DataAccessException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof SQLException) throw DbErrorTranslator.translate((SQLException) cause);
            throw ex;
        }
    }

    private Integer callCreateBatch(Connection con, Integer productId, String batchNumber,
                                    java.time.LocalDate manufactureDate, java.time.LocalDate expirationDate,
                                    String attributesJson) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{call inventory.usp_ProductBatch_CreateIfNotExists(?,?,?,?,?,?)}")) {
            // SP signature: @ProductID, @BatchNumber, @ManufactureDate, @ExpirationDate, @Attributes, @BatchID OUTPUT
            cs.setInt(1, productId);
            cs.setString(2, batchNumber);
            if (manufactureDate != null) cs.setDate(3, java.sql.Date.valueOf(manufactureDate)); else cs.setNull(3, Types.DATE);
            if (expirationDate != null) cs.setDate(4, java.sql.Date.valueOf(expirationDate)); else cs.setNull(4, Types.DATE);
            if (attributesJson != null) cs.setString(5, attributesJson); else cs.setNull(5, Types.NVARCHAR);
            cs.registerOutParameter(6, Types.INTEGER);
            cs.execute();
            int id = cs.getInt(6);
            return id == 0 ? null : id;
        }
    }

    private Integer callCreateSerial(Connection con, Integer productId, String serialNumber) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{call inventory.usp_ProductSerial_CreateIfNotExists(?,?,?,?,?)}")) {
            // @ProductID, @SerialNumber, @CurrentStatus, @Attributes, @SerialID OUTPUT
            cs.setInt(1, productId);
            cs.setString(2, serialNumber);
            cs.setString(3, "AVAILABLE");
            cs.setNull(4, Types.NVARCHAR);
            cs.registerOutParameter(5, Types.INTEGER);
            cs.execute();
            int id = cs.getInt(5);
            return id == 0 ? null : id;
        }
    }

    private void callCreateItemLocation(Connection con, Integer warehouseId, Integer productId, Integer batchId, Integer serialId, String locationCode, String notes) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{call inventory.usp_ItemLocation_CreateIfNotExists(?,?,?,?,?,?)}")) {
            if (warehouseId != null) cs.setInt(1, warehouseId); else cs.setNull(1, Types.INTEGER);
            cs.setInt(2, productId);
            if (batchId != null) cs.setInt(3, batchId); else cs.setNull(3, Types.INTEGER);
            if (serialId != null) cs.setInt(4, serialId); else cs.setNull(4, Types.INTEGER);
            cs.setString(5, locationCode);
            cs.setString(6, notes);
            try {
                cs.execute();
            } catch (SQLException ex) {
                // Ignore duplicate-key / unique constraint errors which can occur in concurrent creates
                if (isDuplicateConstraint(ex)) {
                    return;
                }
                throw ex;
            }
        }
    }

    // Detect SQL Server duplicate-key / constraint violation (2627/2601) or SQLState class 23
    private boolean isDuplicateConstraint(SQLException ex) {
        SQLException e = ex;
        while (e != null) {
            int code = e.getErrorCode();
            String sqlState = e.getSQLState();
            if (code == 2627 || code == 2601) return true;
            if (sqlState != null && sqlState.startsWith("23")) return true;
            e = e.getNextException();
        }
        return false;
    }

    private SQLServerDataTable buildLineTvp(List<InventoryLineCommand> lines) throws SQLException {
        SQLServerDataTable tvp = new SQLServerDataTable();
        tvp.addColumnMetadata("ProductID", java.sql.Types.INTEGER);
        tvp.addColumnMetadata("BatchID", java.sql.Types.INTEGER);
        tvp.addColumnMetadata("SerialID", java.sql.Types.INTEGER);
        tvp.addColumnMetadata("Quantity", java.sql.Types.DECIMAL);
        tvp.addColumnMetadata("UnitCost", java.sql.Types.DECIMAL);
        tvp.addColumnMetadata("Notes", java.sql.Types.NVARCHAR);
        for (InventoryLineCommand l : lines) {
            Object q = l.getQuantity() != null ? l.getQuantity() : null;
            // Nota: ya no incluimos LocationCode en el TVP. La creación de ubicaciones se realiza
            // exclusivamente mediante callCreateItemLocation cuando command.isAutoCreateLocation() es true.
            tvp.addRow(l.getProductId(), l.getBatchId(), l.getSerialId(), q, l.getUnitCost(), l.getNotes());
        }
        return tvp;
    }

    private long callWrapperNonSerial(Connection con, String movement, PostInventoryMovementCommand command, SQLServerDataTable tvp, int createdBy) throws SQLException {
        String sp;
        boolean isIn = "IN".equalsIgnoreCase(movement) || "OPENING".equalsIgnoreCase(movement);
        boolean isOut = "OUT".equalsIgnoreCase(movement);
        boolean isTrf = "TRF".equalsIgnoreCase(movement);
        boolean isAdj = "ADJ".equalsIgnoreCase(movement);

        if (isIn) sp = "inventory.usp_Inventory_PostIN";
        else if (isOut) sp = "inventory.usp_Inventory_PostOUT";
        else if (isTrf) sp = "inventory.usp_Inventory_PostTRF";
        else if (isAdj) sp = "inventory.usp_Inventory_PostADJ";
        else throw new IllegalArgumentException("unsupported movement type: " + movement);

        String callSql;
        if (isAdj) {
            callSql = "{call " + sp + "(?,?,?,?,?,?,?)}"; // 7 params for ADJ (6 inputs + 1 output)
        } else {
            callSql = "{call " + sp + "(?,?,?,?,?,?,?,?)}"; // 8 params for IN/OUT/TRF
        }
        try (CallableStatement cs = con.prepareCall(callSql)) {
            int idx = 1;
            if (isIn) {
                // SP signature: @ToWarehouseID, @MovementDate, @ReferenceNumber, @CreatedBy, @SupplierID, @InvoiceID, @Lines, @MovementID
                if (command.getToWarehouseId() != null) cs.setInt(idx++, command.getToWarehouseId()); else cs.setNull(idx++, Types.INTEGER);
                cs.setTimestamp(idx++, command.getMovementDate() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.from(command.getMovementDate().toInstant()));
                cs.setNull(idx++, Types.NVARCHAR); // ReferenceNumber (not used)
                cs.setInt(idx++, createdBy); // CreatedBy as INT
                cs.setNull(idx++, Types.INTEGER); // SupplierID (not used)
                if (command.getInvoiceId() != null) cs.setInt(idx++, command.getInvoiceId()); else cs.setNull(idx++, Types.INTEGER);
            } else if (isOut) {
                // @FromWarehouseID, @MovementDate, @invoiceID, @TemporalID, @CreatedBy, @CustomerID, @Lines, @MovementID
                if (command.getFromWarehouseId() != null) cs.setInt(idx++, command.getFromWarehouseId()); else cs.setNull(idx++, Types.INTEGER);
                cs.setTimestamp(idx++, command.getMovementDate() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.from(command.getMovementDate().toInstant()));
                if (command.getInvoiceId() != null) cs.setInt(idx++, command.getInvoiceId()); else cs.setNull(idx++, Types.INTEGER);
                if (command.getTemporalId() != null) cs.setInt(idx++, command.getTemporalId()); else cs.setNull(idx++, Types.INTEGER);
                cs.setInt(idx++, createdBy);
                cs.setNull(idx++, Types.INTEGER);
            } else if (isTrf) {
                // @FromWarehouseID, @ToWarehouseID, @MovementDate, @invoiceID, @TemporalID, @CreatedBy, @Lines, @MovementID
                if (command.getFromWarehouseId() != null) cs.setInt(idx++, command.getFromWarehouseId()); else cs.setNull(idx++, Types.INTEGER);
                if (command.getToWarehouseId() != null) cs.setInt(idx++, command.getToWarehouseId()); else cs.setNull(idx++, Types.INTEGER);
                cs.setTimestamp(idx++, command.getMovementDate() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.from(command.getMovementDate().toInstant()));
                if (command.getInvoiceId() != null) cs.setInt(idx++, command.getInvoiceId()); else cs.setNull(idx++, Types.INTEGER);
                if (command.getTemporalId() != null) cs.setInt(idx++, command.getTemporalId()); else cs.setNull(idx++, Types.INTEGER);
                cs.setInt(idx++, createdBy);
            } else { // ADJ
                // @WarehouseID, @MovementDate, @invoiceID, @TemporalID, @CreatedBy, @Lines, @MovementID
                if (command.getFromWarehouseId() != null) cs.setInt(idx++, command.getFromWarehouseId()); else if (command.getToWarehouseId() != null) cs.setInt(idx++, command.getToWarehouseId()); else cs.setNull(idx++, Types.INTEGER);
                cs.setTimestamp(idx++, command.getMovementDate() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.from(command.getMovementDate().toInstant()));
                if (command.getInvoiceId() != null) cs.setInt(idx++, command.getInvoiceId()); else cs.setNull(idx++, Types.INTEGER);
                if (command.getTemporalId() != null) cs.setInt(idx++, command.getTemporalId()); else cs.setNull(idx++, Types.INTEGER);
                cs.setInt(idx++, createdBy);
            }

            // set TVP param index depends on above; assume next is TVP
            cs.setObject(idx++, tvp);
            cs.registerOutParameter(idx, Types.BIGINT);
            cs.execute();
            return cs.getLong(idx);
        }
    }

    private long callWrapperSerial(Connection con, String movement, PostInventoryMovementCommand command, SQLServerDataTable tvp, int createdBy) throws SQLException {
        String sp;
        switch (movement) {
            case "IN": sp = "inventory.usp_Inventory_PostIN_Serial"; break;
            case "OUT": sp = "inventory.usp_Inventory_PostOUT_Serial"; break;
            case "TRF": sp = "inventory.usp_Inventory_PostTRF_Serial"; break;
            case "ADJ": sp = "inventory.usp_Inventory_PostADJ_Serial"; break;
            default: throw new IllegalArgumentException("unsupported movement type: " + movement);
        }

        // ADJ has 7 params (no supplier/customer); others (IN/OUT/TRF) 8
    String callSql = "{call " + sp + "(" + ("ADJ".equalsIgnoreCase(movement) ? "?,?,?,?,?,?,?" : "?,?,?,?,?,?,?,?") + ")}";
        try (CallableStatement cs = con.prepareCall(callSql)) {
            int idx = 1;
            if ("IN".equalsIgnoreCase(movement)) {
                if (command.getToWarehouseId() != null) cs.setInt(idx++, command.getToWarehouseId()); else cs.setNull(idx++, Types.INTEGER);
            } else if ("OUT".equalsIgnoreCase(movement)) {
                if (command.getFromWarehouseId() != null) cs.setInt(idx++, command.getFromWarehouseId()); else cs.setNull(idx++, Types.INTEGER);
            } else if ("TRF".equalsIgnoreCase(movement)) {
                if (command.getFromWarehouseId() != null) cs.setInt(idx++, command.getFromWarehouseId()); else cs.setNull(idx++, Types.INTEGER);
                if (command.getToWarehouseId() != null) cs.setInt(idx++, command.getToWarehouseId()); else cs.setNull(idx++, Types.INTEGER);
            } else { // ADJ
                if (command.getFromWarehouseId() != null) cs.setInt(idx++, command.getFromWarehouseId()); else if (command.getToWarehouseId() != null) cs.setInt(idx++, command.getToWarehouseId()); else cs.setNull(idx++, Types.INTEGER);
            }

            cs.setTimestamp(idx++, command.getMovementDate() == null ? new Timestamp(System.currentTimeMillis()) : Timestamp.from(command.getMovementDate().toInstant()));
            // For IN serial: ReferenceNumber, CreatedBy, SupplierID, InvoiceID
            if ("IN".equalsIgnoreCase(movement)) {
                cs.setNull(idx++, Types.NVARCHAR); // ReferenceNumber
                cs.setInt(idx++, createdBy); // CreatedBy as INT
                cs.setNull(idx++, Types.INTEGER); // SupplierID
                if (command.getInvoiceId() != null) cs.setInt(idx++, command.getInvoiceId()); else cs.setNull(idx++, Types.INTEGER);
            } else {
                // For other movements keep existing logic
                if (command.getInvoiceId() != null) cs.setInt(idx++, command.getInvoiceId()); else cs.setNull(idx++, Types.INTEGER);
                if (command.getTemporalId() != null) cs.setInt(idx++, command.getTemporalId()); else cs.setNull(idx++, Types.INTEGER);
                cs.setInt(idx++, createdBy);
                // supplier/customer param only for OUT
                if ("OUT".equalsIgnoreCase(movement)) {
                    cs.setNull(idx++, Types.INTEGER);
                }
            }
            cs.setObject(idx++, tvp);
            cs.registerOutParameter(idx, Types.BIGINT);
            cs.execute();
            return cs.getLong(idx);
        }
    }

    private Integer determineLocationWarehouse(String movementType, Integer from, Integer to, java.math.BigDecimal qty) {
        if ("IN".equalsIgnoreCase(movementType) || "OPENING".equalsIgnoreCase(movementType)) return to;
        if ("OUT".equalsIgnoreCase(movementType)) return from;
        if ("TRF".equalsIgnoreCase(movementType)) return to;
        // ADJ: choose non-null
        return from != null ? from : to;
    }

    /**
     * Pre-validation for IN movements with InvoiceID.
     * Calls usp_InvoiceItem_GetCantidad to verify the product exists in the invoice
     * and compares the expected quantity with the movement's total quantity.
     * Appends a mismatch note if quantities don't match.
     */
    private List<InventoryLineCommand> preValidateInvoice(
            Connection con,
            Integer invoiceId,
            List<InventoryLineCommand> lines
    ) throws SQLException {
        if (lines.isEmpty()) {
            return lines;
        }

        // All lines belong to the same ProductID (confirmed by user)
        Integer productId = lines.get(0).getProductId();

        // Calculate total quantity: sum of all line quantities
        BigDecimal totalQuantity = BigDecimal.ZERO;
        for (InventoryLineCommand line : lines) {
            BigDecimal lineQty = line.getQuantity() != null ? line.getQuantity() : BigDecimal.ONE;
            totalQuantity = totalQuantity.add(lineQty);
        }

        // Call SP to get expected quantity from Invoice
        BigDecimal expectedCantidad = callGetInvoiceItemCantidad(con, invoiceId, productId);

        // Check for mismatch
        boolean mismatch = totalQuantity.compareTo(expectedCantidad) != 0;

        // If mismatch, append note to all lines
        if (mismatch) {
            String mismatchMessage = "Este movimiento no coincide totalmente con lo declarado en la factura.";
            List<InventoryLineCommand> updatedLines = new ArrayList<>();
            for (InventoryLineCommand line : lines) {
                String currentNotes = line.getNotes();
                if (currentNotes != null && !currentNotes.trim().isEmpty()) {
                    line.setNotes(currentNotes + " | " + mismatchMessage);
                } else {
                    line.setNotes(mismatchMessage);
                }
                updatedLines.add(line);
            }
            return updatedLines;
        }

        return lines;
    }

    /**
     * Calls finance.usp_InvoiceItem_GetCantidad.
     * Returns the expected Cantidad for the product in the invoice.
     * Throws SQLException if the product is not found in the invoice.
     */
    private BigDecimal callGetInvoiceItemCantidad(
            Connection con,
            Integer invoiceId,
            Integer productId
    ) throws SQLException {
        try (CallableStatement cs = con.prepareCall("{call finance.usp_InvoiceItem_GetCantidad(?,?)}")) {
            cs.setInt(1, invoiceId);
            cs.setInt(2, productId);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("Cantidad");
                } else {
                    throw new SQLException("El producto no existe en la factura especificada");
                }
            }
        }
    }
}
