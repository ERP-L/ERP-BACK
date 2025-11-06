package com.app.erp.inventory.infrastructure.repository.sp;

import com.app.erp.inventory.application.dtos.commands.CreateWarehouseCommand;
import com.app.erp.inventory.application.dtos.results.CreateWarehouseResult;
import com.app.erp.inventory.application.port.WarehouseWritePort;
import com.app.erp.inventory.infrastructure.mapper.WarehouseRowMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.sql.Types;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.app.erp.shared.exceptions.ApplicationException;
import com.app.erp.shared.exceptions.InvalidInputException;
import com.app.erp.shared.exceptions.ResourceConflictException;

@Repository
public class WarehouseWriteGateway implements WarehouseWritePort {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall createCall;

    public WarehouseWriteGateway(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void init() {
        this.createCall = new SimpleJdbcCall(this.jdbc)
                .withSchemaName("inventory")
                .withProcedureName("usp_Warehouse_Create")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("BranchID", Types.INTEGER),
                        new SqlParameter("WarehouseCode", Types.NVARCHAR),
                        new SqlParameter("WarehouseName", Types.NVARCHAR),
                        new SqlParameter("Address", Types.NVARCHAR),
                        new SqlParameter("Phone", Types.NVARCHAR),
                        new SqlParameter("Contact", Types.NVARCHAR),
                        new SqlParameter("IsActive", Types.BIT),
                        new SqlParameter("CreatedUtc", Types.TIMESTAMP),
                        new SqlOutParameter("NewWarehouseID", Types.INTEGER)
                )
                .returningResultSet("result", new WarehouseRowMapper());
    }

    @Override
    public CreateWarehouseResult createWarehouse(CreateWarehouseCommand cmd) {
        Map<String, Object> in = new HashMap<>();
        in.put("BranchID", cmd.getBranchId());
        in.put("WarehouseCode", blankToNull(cmd.getWarehouseCode()));
        in.put("WarehouseName", cmd.getWarehouseName());
        in.put("Address", blankToNull(cmd.getAddress()));
        in.put("Phone", blankToNull(cmd.getPhone()));
        in.put("Contact", blankToNull(cmd.getContact()));
        in.put("IsActive", cmd.getIsActive() == null ? Boolean.TRUE : cmd.getIsActive());
        in.put("CreatedUtc", instantToTimestamp(cmd.getCreatedUtc()));

        try {
            Map<String, Object> out = createCall.execute(in);

            @SuppressWarnings("unchecked")
            List<CreateWarehouseResult> rows =
                    (List<CreateWarehouseResult>) out.get("result");

            if (rows != null && !rows.isEmpty()) {
                return rows.get(0);
            }

            // Fallback si no llegó resultset (no debería pasar)
            Integer id = (Integer) out.get("NewWarehouseID");
            if (id != null) {
                return jdbc.queryForObject(
                        "SELECT WarehouseID, WarehouseCode, WarehouseName, Address, Phone, Contact, IsActive, CreatedUtc, BranchID " +
                                "FROM inventory.Warehouse WHERE WarehouseID = ?",
                        new WarehouseRowMapper(),
                        id
                );
            }

            throw new ApplicationException("No se recibió resultado del SP.");
        } catch (DataAccessException dae) {
            translateAndThrow(dae);
            throw dae; // inalcanzable
        }
    }

    // ===== helpers =====

    private static String blankToNull(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private static Timestamp instantToTimestamp(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }

    private static final Pattern PS_ERRNO =
            Pattern.compile("failed \\((\\d+), line \\d+\\):", Pattern.CASE_INSENSITIVE);

    private static void translateAndThrow(DataAccessException dae) {
        String msg = dae.getMessage() == null ? "" : dae.getMessage();
        Integer code = extractErrorCode(msg);

        if (code != null) {
            switch (code) {
                case 50001: // WarehouseName requerido
                case 50002: // BranchID requerido
                    throw new InvalidInputException(extractReadableMessage(msg, "Entrada inválida."));
                case 50003: // Duplicado WarehouseCode en la misma Branch
                    throw new ResourceConflictException(extractReadableMessage(msg, "WarehouseCode duplicado."));
                default:
                    throw new ApplicationException(extractReadableMessage(msg, "Error en SP."));
            }
        }

        throw new ApplicationException(extractReadableMessage(msg, "Error de base de datos."));
    }

    private static Integer extractErrorCode(String msg) {
        Matcher m = PS_ERRNO.matcher(msg == null ? "" : msg);
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); } catch (NumberFormatException ignored) {}
        }
        if (msg != null) {
            if (msg.contains("50001")) return 50001;
            if (msg.contains("50002")) return 50002;
            if (msg.contains("50003")) return 50003;
        }
        return null;
    }

    private static String extractReadableMessage(String full, String fallback) {
        if (full == null || full.isBlank()) return fallback;
        int idx = full.indexOf("): ");
        if (idx < 0) idx = full.indexOf("):");
        if (idx > 0 && idx + 2 < full.length()) {
            return full.substring(idx + 2).trim();
        }
        return full;
    }
}
