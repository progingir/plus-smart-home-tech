package ru.yandex.practicum.kafka.telemetry.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.kafka.telemetry.model.hub.enums.ConditionOperation;
import ru.yandex.practicum.kafka.telemetry.model.hub.enums.ConditionType;

@Getter @Setter @ToString
public class ScenarioCondition {

    @NotBlank
    String sensorId;

    @NotNull
    ConditionType type;

    @NotNull
    ConditionOperation operation;

    int value;

}
