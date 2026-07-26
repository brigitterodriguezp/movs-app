package com.movsapp.backend.dto;

import java.util.Map;

public record OllamaGenerateRequest(
    String model,
    String prompt,
    boolean stream,
    Map<String, Object> options
) {}
