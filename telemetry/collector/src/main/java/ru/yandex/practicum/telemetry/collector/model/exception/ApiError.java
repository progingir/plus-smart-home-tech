package ru.yandex.practicum.telemetry.collector.model.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiError {
    @JsonProperty("error")
    private String error;

    @JsonProperty("description")
    private String description;


    public ApiError(String error, String description) {
        this.error = error;
        this.description = description;
    }
}