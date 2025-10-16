package com.app.erp.inventory.infrastructure.sp;

import com.app.erp.inventory.application.internal.messages.commands.CreateProductCommand;
import com.app.erp.inventory.application.internal.messages.results.CreateProductResult;
import com.app.erp.inventory.application.internal.port.ProductWritePort;
import com.app.erp.inventory.infrastructure.mappers.ProductRowMapper;
import com.app.erp.shared.exceptions.ApplicationException;
import com.app.erp.shared.exceptions.InvalidInputException;
import com.app.erp.shared.exceptions.NotFoundException;
import com.app.erp.shared.exceptions.ResourceConflictException;
import jakarta.annotation.PostConstruct;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Llama al SP inventory.usp_Product_Create y traduce errores 5100x a excepciones shared. */
@Repository
public class ProductWriteGateway implements ProductWritePort {

    private final JdbcTemplate jdbc;
    private SimpleJdbcCall createCall;

    public ProductWriteGateway(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @PostConstruct
    void init() {
        this.createCall = new SimpleJdbcCall(this.jdbc)
                .withSchemaName("inventory")
                .withProcedureName("usp_Product_Create")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("CompanyID", Types.INTEGER),
                        new SqlParameter("SKU", Types.NVARCHAR),
                        new SqlParameter("ProductName", Types.NVARCHAR),
                        new SqlParameter("CategoryID", Types.INTEGER),
                        new SqlParameter("UOMID", Types.INTEGER),
                        new SqlParameter("IsSerialized", Types.BIT),
                        new SqlParameter("IsBatchControlled", Types.BIT),
                        new SqlParameter("ReorderLevel", Types.DECIMAL),
                        new SqlParameter("LeadTimeDays", Types.INTEGER),
                        new SqlParameter("Weight", Types.DECIMAL),
                        new SqlParameter("Volume", Types.DECIMAL),
                        new SqlParameter("Status", Types.INTEGER),
                        new SqlParameter("CreatedUtc", Types.TIMESTAMP),
                        new SqlOutParameter("NewProductID", Types.INTEGER)
                )
                .returningResultSet("result", new ProductRowMapper());
    }

    @Override
    public CreateProductResult createProduct(CreateProductCommand cmd) {
        Map<String, Object> in = new HashMap<>();
        in.put("CompanyID", cmd.getCompanyId());                         // lo fija la app desde el token
        in.put("SKU", nullIfBlank(cmd.getSku()));
        in.put("ProductName", cmd.getProductName());
        in.put("CategoryID", cmd.getCategoryId());
        in.put("UOMID", cmd.getUomId());
        in.put("IsSerialized", toBit(cmd.getIsSerialized()));
        in.put("IsBatchControlled", toBit(cmd.getIsBatchControlled()));
        in.put("ReorderLevel", cmd.getReorderLevel());
        in.put("LeadTimeDays", cmd.getLeadTimeDays());
        in.put("Weight", cmd.getWeight());
        in.put("Volume", cmd.getVolume());
        in.put("Status", cmd.getStatus());                               // la app impone 1
        in.put("CreatedUtc", instantToTimestamp(cmd.getCreatedUtc()));   // la app impone now()

        try {
            Map<String, Object> out = createCall.execute(in);

            @SuppressWarnings("unchecked")
            List<CreateProductResult> rows = (List<CreateProductResult>) out.get("result");
            if (rows != null && !rows.isEmpty()) {
                return rows.get(0);
            }

            Integer id = (Integer) out.get("NewProductID");
            if (id != null) {
                return jdbc.queryForObject(
                        "SELECT ProductID, SKU, ProductName, CategoryID, UOMID," +
                                " IsSerialized, IsBatchControlled, ReorderLevel, LeadTimeDays, Weight, Volume," +
                                " Status, CreatedUtc, UpdatedUtc, CompanyID " +
                                "FROM inventory.Product WHERE ProductID = ?",
                        new ProductRowMapper(),
                        id
                );
            }

            throw new ApplicationException("No se recibió resultado del SP (usp_Product_Create).");
        } catch (DataAccessException dae) {
            translateAndThrow(dae);
            throw dae; // inalcanzable
        }
    }

    // ===== helpers =====

    private static String nullIfBlank(String s) {
        return (s == null || s.trim().isEmpty()) ? null : s.trim();
    }

    private static Timestamp instantToTimestamp(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }

    private static Integer toBit(Boolean b) {
        return b == null ? null : (b ? 1 : 0);
    }

    private static final Pattern PS_ERRNO =
            Pattern.compile("failed \\((\\d+), line \\d+\\):", Pattern.CASE_INSENSITIVE);

    private static void translateAndThrow(DataAccessException dae) {
        String msg = dae.getMessage() == null ? "" : dae.getMessage();
        Integer code = extractErrorCode(msg);

        if (code != null) {
            switch (code) {
                // Reglas básicas
                case 51001: // CompanyID requerido
                case 51002: // ProductName requerido
                case 51003: // UOMID requerido
                case 51004: // No puede ser serializado y batch a la vez
                    throw new InvalidInputException(extractReadableMessage(msg, "Entrada inválida."));

                    // FKs no encontrados
                case 51005: // UOMID no existe
                case 51006: // CompanyID no existe (si aplica)
                    throw new NotFoundException(extractReadableMessage(msg, "Recurso no encontrado."));

                    // Unicidades por compañía
                case 51008: // SKU duplicado
                case 51009: // ProductName duplicado
                    throw new ResourceConflictException(extractReadableMessage(msg, "Conflicto de unicidad."));
            }
        }

        throw new ApplicationException(extractReadableMessage(msg, "Error al ejecutar usp_Product_Create."));
    }

    private static Integer extractErrorCode(String msg) {
        Matcher m = PS_ERRNO.matcher(msg == null ? "" : msg);
        if (m.find()) {
            try { return Integer.parseInt(m.group(1)); } catch (NumberFormatException ignored) {}
        }
        if (msg != null) {
            if (msg.contains("51001")) return 51001;
            if (msg.contains("51002")) return 51002;
            if (msg.contains("51003")) return 51003;
            if (msg.contains("51004")) return 51004;
            if (msg.contains("51005")) return 51005;
            if (msg.contains("51006")) return 51006;
            if (msg.contains("51008")) return 51008;
            if (msg.contains("51009")) return 51009;
        }
        return null;
    }

    private static String extractReadableMessage(String full, String fallback) {
        if (full == null || full.isBlank()) return fallback;
        int idx = full.indexOf("):");
        if (idx > 0 && idx + 2 < full.length()) {
            return full.substring(idx + 2).trim();
        }
        return full;
    }
}
