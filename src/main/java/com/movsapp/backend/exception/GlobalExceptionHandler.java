package com.movsapp.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    ResponseEntity<ApiError> notFound(RecursoNoEncontradoException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), req, Map.of());
    }
    @ExceptionHandler(ConflictoException.class)
    ResponseEntity<ApiError> conflict(ConflictoException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), req, Map.of());
    }
    @ExceptionHandler(NoAutorizadoException.class)
    ResponseEntity<ApiError> unauthorized(NoAutorizadoException ex, HttpServletRequest req) {
        return error(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, Map.of());
    }
    @ExceptionHandler(ProhibidoException.class)
    ResponseEntity<ApiError> forbidden(ProhibidoException ex, HttpServletRequest req) {
        return error(HttpStatus.FORBIDDEN, ex.getMessage(), req, Map.of());
    }
    @ExceptionHandler(SolicitudInvalidaException.class)
    ResponseEntity<ApiError> badRequest(SolicitudInvalidaException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), req, Map.of());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> fields.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "La solicitud contiene campos inválidos.", req, fields);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiError> integrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, "La operación viola una restricción de integridad.", req, Map.of());
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> unexpected(Exception ex, HttpServletRequest req) {
        log.error("Error interno no controlado", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno.", req, Map.of());
    }
    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest req, Map<String,String> fields) {
        ApiError body = new ApiError(OffsetDateTime.now(), status.value(), status.getReasonPhrase(), message,
                                     req.getRequestURI(), fields);
        return ResponseEntity.status(status).body(body);
    }
}
