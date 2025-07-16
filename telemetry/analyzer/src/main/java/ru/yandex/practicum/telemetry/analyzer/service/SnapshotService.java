package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.analyzer.entity.Action;
import ru.yandex.practicum.telemetry.analyzer.entity.ConditionOperation;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioCondition;
import ru.yandex.practicum.telemetry.analyzer.processor.HubRouterProcessor;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnapshotService {

    private ScenarioRepository scenarioRepository;
    private HubRouterProcessor hubRouterProcessor;

    public void analyze(SensorsSnapshotAvro sensorsSnapshot) {

        log.info("Analyzing sensors snapshot {}", sensorsSnapshot);

        String hubId = sensorsSnapshot.getHubId();

        Set<Scenario> hubScenario = scenarioRepository.findByHubId(hubId);

        for (Scenario scenario : hubScenario) {
            if(isScenarioTriggered(scenario, sensorsSnapshot)) {
                log.info("Scenario {} triggered", scenario);
                executeActions(scenario.getScenarioActions(), hubId);
            }
        }

    }

    private boolean isScenarioTriggered(Scenario scenario, SensorsSnapshotAvro sensorsSnapshot) {
        for (ScenarioCondition condition : scenario.getScenarioConditions()) {
            if(!isConditionTriggered(condition, sensorsSnapshot)) {
                return false;
            }
        }
        return true;
    }

    private boolean isConditionTriggered(ScenarioCondition condition, SensorsSnapshotAvro snapshot) {
        SensorStateAvro sensorState = snapshot.getSensorsState().get(condition.getSensorId());

        switch(condition.getType()) {
            case MOTION -> {
                MotionSensorAvro motionSensorAvro = (MotionSensorAvro) sensorState.getData();
                int motionValue = motionSensorAvro.getMotion() ? 1 : 0;
                return isConditionCompare(motionValue, condition.getOperation(), condition.getValue());
            }
            case SWITCH -> {
                SwitchSensorAvro switchSensor = (SwitchSensorAvro) sensorState.getData();
                int switchStatus = switchSensor.getState() ? 1 : 0;
                return isConditionCompare(switchStatus, condition.getOperation(), condition.getValue());
            }
            case CO2LEVEL -> {
                ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();
                return isConditionCompare(climateSensor.getCo2Level(), condition.getOperation(), condition.getValue());
            }
            case TEMPERATURE -> {
                TemperatureSensorAvro temperatureSensor = (TemperatureSensorAvro) sensorState.getData();
                return isConditionCompare(
                        temperatureSensor.getTemperatureC(), condition.getOperation(), condition.getValue());
            }

            case HUMIDITY -> {
                ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();
                return isConditionCompare(climateSensor.getHumidity(), condition.getOperation(), condition.getValue());
            }

            case LUMINOSITY -> {
                LightSensorAvro lightSensor = (LightSensorAvro) sensorState.getData();
                return isConditionCompare(lightSensor.getLinkQuality(), condition.getOperation(), condition.getValue());
            }

            default -> throw new IllegalStateException("Unexpected value: " + condition.getType());
        }
    }

    private boolean isConditionCompare(int sensorValue, ConditionOperation operation, int conditionValue) {
        return switch(operation) {
            case EQUALS -> sensorValue == conditionValue;
            case GREATER_THAN -> sensorValue > conditionValue;
            case LOWER_THAN -> sensorValue < conditionValue;
        };
    }

    private void executeActions(Set<Action> actions, String hubId) {
        for (Action action : actions) {
            hubRouterProcessor.executeAction(action, hubId);
        }
        log.info("{} actions executed", actions.size());
    }


}
