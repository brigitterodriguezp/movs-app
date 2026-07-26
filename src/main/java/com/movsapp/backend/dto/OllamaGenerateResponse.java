package com.movsapp.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaGenerateResponse(
    String model,
    @JsonProperty("created_at") String createdAt,
    String response,
    boolean done,
    @JsonProperty("done_reason") String doneReason,
    @JsonProperty("total_duration") Long totalDuration,
    @JsonProperty("load_duration") Long loadDuration,
    @JsonProperty("prompt_eval_count") Integer promptEvalCount,
    @JsonProperty("eval_count") Integer evalCount
) {}
