package com.movsapp.backend.exception;
import java.time.OffsetDateTime;
import java.util.Map;
public record ApiError(OffsetDateTime timestamp, int status, String error, String mensaje, String ruta,
                       Map<String, String> validaciones) {}
