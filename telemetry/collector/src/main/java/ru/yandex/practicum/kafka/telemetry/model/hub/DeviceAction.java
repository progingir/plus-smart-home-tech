package ru.yandex.practicum.kafka.telemetry.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.model.hub.enums.ActionType;

@Getter @Setter @ToString
public class DeviceAction {

    @NotBlank
    private String sensorId;

    @NotNull
    private ActionType type;

    private Integer value;

}
