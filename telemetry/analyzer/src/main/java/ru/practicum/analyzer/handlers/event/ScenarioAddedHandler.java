package ru.practicum.analyzer.handlers.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.model.*;
import ru.practicum.analyzer.repository.*;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) event.getPayload();

        Scenario scenario = scenarioRepository
                .findByHubIdAndName(event.getHubId(), scenarioAddedEvent.getName())
                .orElseGet(() -> scenarioRepository.save(
                        Scenario.builder()
                                .hubId(event.getHubId())
                                .name(scenarioAddedEvent.getName())
                                .build()));

        scenarioActionRepository.deleteByScenario(scenario);
        scenarioConditionRepository.deleteByScenario(scenario);

        scenarioAddedEvent.getConditions().forEach(cDto -> {
            Sensor sensor = sensorRepository.findById(cDto.getSensorId())
                    .orElseGet(() -> sensorRepository.save(
                            Sensor.builder()
                                    .id(cDto.getSensorId())
                                    .hubId(event.getHubId())
                                    .build()));
            Condition condition = conditionRepository.save(
                    Condition.builder()
                            .type(cDto.getType())
                            .operation(cDto.getOperation())
                            .value(asInteger(cDto.getValue()))
                            .build());
            scenarioConditionRepository.save(
                    ScenarioCondition.builder()
                            .scenario(scenario)
                            .sensor(sensor)
                            .condition(condition)
                            .id(new ScenarioConditionId(
                                    scenario.getId(),
                                    sensor.getId(),
                                    condition.getId()))
                            .build());
        });

        scenarioAddedEvent.getAction().forEach(aDto -> {
            Sensor sensor = sensorRepository.findById(aDto.getSensorId())
                    .orElseGet(() -> sensorRepository.save(
                            Sensor.builder()
                                    .id(aDto.getSensorId())
                                    .hubId(event.getHubId())
                                    .build()));
            Action action = actionRepository.save(
                    Action.builder()
                            .type(aDto.getType())
                            .value(aDto.getValue())
                            .build());
            scenarioActionRepository.save(
                    ScenarioAction.builder()
                            .scenario(scenario)
                            .sensor(sensor)
                            .action(action)
                            .id(new ScenarioActionId(
                                    scenario.getId(),
                                    sensor.getId(),
                                    action.getId()))
                            .build());
        });

        log.info("Сценарий '{}' для хаба '{}' обновлён: {} условий, {} действий",
                scenario.getName(),
                scenario.getHubId(),
                scenarioAddedEvent.getConditions().size(),
                scenarioAddedEvent.getAction().size());

    }

    @Override
    public String getPayloadType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    private Integer asInteger(Object value) {
        return value instanceof Integer
                ? (Integer) value
                : ((Boolean) value ? 1 : 0);
    }

}
