package ru.practicum.analyzer.handlers.snapshot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.client.ScenarioActionProducer;
import ru.practicum.analyzer.model.Condition;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.model.ScenarioCondition;
import ru.practicum.analyzer.repository.ScenarioActionRepository;
import ru.practicum.analyzer.repository.ScenarioConditionRepository;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SnapshotHandler {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioActionProducer scenarioActionProducer;

    @Transactional(readOnly = true)
    public void handleSnapshot(SensorsSnapshotAvro sensorsSnapshot) {
        Map<String, SensorStateAvro> sensorStateMap = sensorsSnapshot.getSensorsState();
        List<Scenario> scenarios = scenarioRepository.findByHubId(sensorsSnapshot.getHubId());

        scenarios.stream()
                .filter(scenario -> handleScenario(scenario, sensorStateMap))
                .forEach(scenario -> {
                    log.info("Отправка действия для сценария {}", scenario.getName());
                    sendScenarioActions(scenario);
                });
    }

    private boolean handleScenario(Scenario scenario, Map<String, SensorStateAvro> sensorStateMap) {
        List<ScenarioCondition> scenarioConditions =
                scenarioConditionRepository.findByScenario(scenario);
        log.info("Получили список условий {} у сценария name = {}",
                scenarioConditions.size(), scenario.getName());

        /* если хотя бы одно условие не выполняется – сценарий не подходит */
        return scenarioConditions.stream()
                .noneMatch(sc -> !checkCondition(sc.getCondition(),
                        sc.getSensor().getId(),
                        sensorStateMap));
    }

    private boolean checkCondition(Condition condition,
                                   String sensorId,
                                   Map<String, SensorStateAvro> sensorStateMap) {

        SensorStateAvro sensorState = sensorStateMap.get(sensorId);
        if (sensorState == null) {
            return false;
        }

        return switch (condition.getType()) {
            case LUMINOSITY -> {
                LightSensorAvro light = (LightSensorAvro) sensorState.getData();
                yield handleOperation(condition, light.getLuminosity());
            }
            case TEMPERATURE -> {
                ClimateSensorAvro climate = (ClimateSensorAvro) sensorState.getData();
                yield handleOperation(condition, climate.getTemperatureC());
            }
            case MOTION -> {
                MotionSensorAvro motion = (MotionSensorAvro) sensorState.getData();
                yield handleOperation(condition, motion.getMotion() ? 1 : 0);
            }
            case SWITCH -> {
                SwitchSensorAvro sw = (SwitchSensorAvro) sensorState.getData();
                yield handleOperation(condition, sw.getState() ? 1 : 0);
            }
            case CO2LEVEL -> {
                ClimateSensorAvro climate = (ClimateSensorAvro) sensorState.getData();
                yield handleOperation(condition, climate.getCo2Level());
            }
            case HUMIDITY -> {
                ClimateSensorAvro climate = (ClimateSensorAvro) sensorState.getData();
                yield handleOperation(condition, climate.getHumidity());
            }
        };
    }

    private boolean handleOperation(Condition condition, Integer currentValue) {
        Integer targetValue = condition.getValue();
        return switch (condition.getOperation()) {
            case EQUALS       -> targetValue.equals(currentValue);
            case LOWER_THAN   -> currentValue < targetValue;
            case GREATER_THAN -> currentValue > targetValue;
        };
    }

    private void sendScenarioActions(Scenario scenario) {
        scenarioActionRepository.findByScenario(scenario)
                .forEach(scenarioActionProducer::sendAction);
    }

}
