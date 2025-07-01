package ru.yandex.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.ActionType;

@Data
public class DeviceAction {

    @NotBlank
    private String sensorId;

    @NotNull
    private ActionType type;

    @PositiveOrZero
    private Integer value;
}