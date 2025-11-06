package com.app.erp.shared.interfaces.rest;

import com.app.erp.shared.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalRestExceptionHandler {

    private record ErrorResponse(
            OffsetDateTime timestamp,
            int status,
            String error,
            String message,
            String path,
            Map<String, Object> details
    ) {}

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, String path, Map<String, Object> details) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), message, path, details)
        );
    }

    // 400 – body mal formado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleBadBody(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "JSON de entrada inválido", req.getRequestURI(), Map.of("cause", ex.getMostSpecificCause().getMessage()));
    }

    // 400 – @Valid en body (fields)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(fe -> fe.getField(), fe -> fe.getDefaultMessage(), (a, b) -> a, HashMap::new));
        return build(HttpStatus.BAD_REQUEST, "Datos de entrada inválidos", req.getRequestURI(), Map.of("fields", fieldErrors));
    }

    // 400 – @Validated en params/path (si lo usas)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        var details = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(v -> v.getPropertyPath().toString(), v -> v.getMessage(), (a, b) -> a, HashMap::new));
        return build(HttpStatus.BAD_REQUEST, "Parámetros inválidos", req.getRequestURI(), Map.of("violations", details));
    }

    // 403 – autorización
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthz(AuthorizationException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req.getRequestURI(), Map.of());
    }

    // 404 – negocio
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), Map.of());
    }

    // 409 – conflicto de negocio
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ResourceConflictException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI(), Map.of());
    }

    // 400 – entrada inválida de aplicación
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(InvalidInputException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), Map.of());
    }

    // 500 – por defecto; intenta mostrar error real de BD si existe
    @ExceptionHandler({ ApplicationException.class, Exception.class })
    public ResponseEntity<ErrorResponse> handleAny(Exception ex, HttpServletRequest req) {
        java.sql.SQLException sqlEx = findSqlException(ex);
        String message = sqlEx != null ? sqlEx.getMessage() : (ex.getMessage() != null ? ex.getMessage() : "Error interno");
        Map<String, Object> details = new HashMap<>();
        details.put("exception", ex.getClass().getSimpleName());
        if (sqlEx != null) {
            details.put("dbCode", sqlEx.getErrorCode());
            details.put("sqlState", sqlEx.getSQLState());
            // Mapeo de error específico lanzado desde SP cuando warehouse no pertenece a la compañía
            if (sqlEx.getErrorCode() == 61001) {
                return build(HttpStatus.NOT_FOUND, sqlEx.getMessage(), req.getRequestURI(), details);
            }
        }
        return build(HttpStatus.INTERNAL_SERVER_ERROR, message, req.getRequestURI(), details);
    }

    private java.sql.SQLException findSqlException(Throwable ex) {
        Throwable cur = ex;
        int depth = 0;
        while (cur != null && depth < 6) {
            if (cur instanceof java.sql.SQLException) return (java.sql.SQLException) cur;
            cur = cur.getCause();
            depth++;
        }
        return null;
    }
}
