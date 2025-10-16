package com.app.erp.organizations.interfaces.rest;

import com.app.erp.shared.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
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

    // 403
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthz(AuthorizationException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), req.getRequestURI(), Map.of());
    }

    // 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), Map.of());
    }

    // 409
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ResourceConflictException ex, HttpServletRequest req) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI(), Map.of());
    }

    // 400 genérica para entradas inválidas desde application
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(InvalidInputException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), Map.of());
    }

    // 500 por defecto (ApplicationException y cualquier otra)
    @ExceptionHandler({ ApplicationException.class, Exception.class })
    public ResponseEntity<ErrorResponse> handleAny(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno", req.getRequestURI(),
                Map.of("exception", ex.getClass().getSimpleName()));
    }
}
