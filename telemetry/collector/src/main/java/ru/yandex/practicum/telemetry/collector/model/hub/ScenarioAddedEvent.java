package ru.yandex.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.HubEventType;

import java.util.List;

@Getter @Setter @ToString(callSuper=true)
public class ScenarioAddedEvent extends HubEvent {

    @NotBlank
    private String name;

    List<ScenarioCondition> conditions;

    List<DeviceAction> actions;


    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
