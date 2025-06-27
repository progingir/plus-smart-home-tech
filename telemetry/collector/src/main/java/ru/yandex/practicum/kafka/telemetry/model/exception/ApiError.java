package ru.yandex.practicum.kafka.telemetry.model.exception;


public class ApiError {
    String error;
    String description;

    public ApiError(String error, String description) {
        this.error = error;
        this.description = description;
    }

}
