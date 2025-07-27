package ru.yandex.practicum.telemetry.collector.service.handler;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.telemetry.collector.model.exception.ApiError;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    public StatusRuntimeException handleException(final Exception e) {
        log.warn("Error", e);
        return new StatusRuntimeException(Status.INTERNAL.withDescription(new ApiError("Error", e.getMessage()).toString()));
    }
}
