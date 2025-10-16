// com/app/erp/iam/interfaces/rest/controller/GlobalErrors.java
package com.app.erp.iam.interfaces.rest.controller;

import com.app.erp.shared.dtos.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestControllerAdvice(basePackages = "com.app.erp") // 👈 limita el alcance a *tus* controllers
public class GlobalErrors {

    /* ---------- Handlers específicos útiles ---------- */

    // 400 para errores de validación (si usas @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (var fe : ex.getBindingResult().getFieldErrors()) {
            fields.put(fe.getField(), fe.getDefaultMessage());
        }
        ApiError body = new ApiError(
                400,
                "VALIDATION_ERROR",
                fields.toString(),
                req.getRequestURI(),
                OffsetDateTime.now().toString()
        );
        return ResponseEntity.badRequest().body(body);
    }

    /* ---------- Catch-all (refleja lo que salga del SP/JDBC) ---------- */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> any(Exception ex, HttpServletRequest req) throws Exception {
        // NO toques swagger
        String p = req.getRequestURI();
        if (p.equals("/swagger-ui.html") || p.startsWith("/swagger-ui") || p.startsWith("/v3/api-docs")) {
            throw ex;
        }

        Throwable root = rootCause(ex);
        String raw = root.getMessage() != null ? root.getMessage() : ex.getMessage();

        int dbCode = (root instanceof SQLException se) ? se.getErrorCode() : 0;
        String sqlState = (root instanceof SQLException se) ? se.getSQLState() : null;

        // 409 para duplicados/unique; 400 para errores de negocio detectables; 500 por defecto
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (isDuplicate(raw, dbCode)) {
            status = HttpStatus.CONFLICT;
        } else if (looksLikeBusiness(raw)) {
            status = HttpStatus.BAD_REQUEST;
        }

        ApiError body = new ApiError(
                status.value(),
                extractCode(raw, dbCode, sqlState),
                sanitize(raw),
                req.getRequestURI(),
                OffsetDateTime.now().toString()
        );
        return ResponseEntity.status(status).body(body);
    }

    /* ---------- helpers ---------- */

    private static Throwable rootCause(Throwable t) {
        Throwable x = t;
        while (x.getCause() != null) x = x.getCause();
        return x;
    }

    private static boolean isDuplicate(String raw, int dbCode) {
        if (raw == null) return false;
        return raw.contains("ALREADY_EXISTS")
                || raw.toLowerCase().contains("duplicate")
                || raw.toUpperCase().contains("UNIQUE")
                || dbCode == 2627 || dbCode == 2601; // SQL Server duplicate key
    }

    // Si el mensaje parece un “negocio” de SP (contiene algo tipo MAYUS_MAYUS)
    private static boolean looksLikeBusiness(String raw) {
        if (raw == null) return false;
        return Pattern.compile("\\b[A-Z_]{3,}\\b").matcher(raw).find();
    }

    private static String extractCode(String raw, int dbCode, String sqlState) {
        if (raw != null) {
            var m = Pattern.compile("\\b[A-Z_]{3,}\\b").matcher(raw);
            if (m.find()) return m.group(0);
        }
        if (dbCode != 0) return "DB_" + dbCode;
        if (sqlState != null) return "SQLSTATE_" + sqlState;
        return "UNKNOWN";
    }

    private static String sanitize(String raw) {
        if (raw == null) return null;
        return raw.length() > 500 ? raw.substring(0, 500) + "..." : raw;
    }
}
