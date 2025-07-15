package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;

import java.util.List;

@Component
public class ScenarioAddedHandler extends BaseHubHandler {

    private static final Logger log = LoggerFactory.getLogger(ScenarioAddedHandler.class);

    public ScenarioAddedHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    ScenarioAddedEventAvro toAvro(HubEventProto hubEvent) {
        log.info("Converting to Avro ScenarioAddedEvent: {}", hubEvent);

        ScenarioAddedEventProto addedScenarioEvent = hubEvent.getScenarioAdded();
        List<DeviceActionAvro> actionAvroList = addedScenarioEvent.getActionList().stream()
                .map(this::toDeviceActionAvro)
                .toList();
        List<ScenarioConditionAvro> scenarioConditionAvroList = addedScenarioEvent.getConditionList().stream()
                .map(this::toScenarioConditionAvro)
                .toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(addedScenarioEvent.getName())
                .setAction(actionAvroList)
                .setConditions(scenarioConditionAvroList)
                .build();
    }

    private DeviceActionAvro toDeviceActionAvro(DeviceActionProto deviceAction) {

        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(toActionTypeAvro(deviceAction.getType()))
                .setValue(deviceAction.getValue())
                .build();
    }

    private ActionTypeAvro toActionTypeAvro(ActionTypeProto actionType) {
        return switch (actionType) {
            case ActionTypeProto.ACTIVATE -> ActionTypeAvro.ACTIVATE;
            case ActionTypeProto.DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
            case ActionTypeProto.INVERSE -> ActionTypeAvro.INVERSE;
            case ActionTypeProto.SET_VALUE -> ActionTypeAvro.SET_VALUE;
            default -> null;
        };
    }

    private ScenarioConditionAvro toScenarioConditionAvro(ScenarioConditionProto scenarioCondition) {
        ScenarioConditionProto.ValueCase valueCase = scenarioCondition.getValueCase();
        Object value;
        if (valueCase == ScenarioConditionProto.ValueCase.INT_VALUE) {
            value = scenarioCondition.getIntValue();
        } else {
            value = scenarioCondition.getBoolValue();
        }

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setType(toConditionTypeAvro(scenarioCondition.getType()))
                .setValue(value)
                .setOperation(toConditionOperationAvro(scenarioCondition.getOperation()))
                .build();
    }

    private ConditionTypeAvro toConditionTypeAvro(ConditionTypeProto conditionType) {
        return switch (conditionType) {
            case ConditionTypeProto.MOTION -> ConditionTypeAvro.MOTION;
            case ConditionTypeProto.LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
            case ConditionTypeProto.SWITCH -> ConditionTypeAvro.SWITCH;
            case ConditionTypeProto.TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
            case ConditionTypeProto.CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
            case ConditionTypeProto.HUMIDITY -> ConditionTypeAvro.HUMIDITY;
            default -> null;
        };
    }

    private ConditionOperationAvro toConditionOperationAvro(ConditionOperationProto conditionOperation) {
        return switch (conditionOperation) {
            case ConditionOperationProto.EQUALS -> ConditionOperationAvro.EQUALS;
            case ConditionOperationProto.GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
            case ConditionOperationProto.LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
            default -> null;
        };
    }


}