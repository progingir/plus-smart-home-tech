package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;

import java.util.List;

@Component
public class ScenarioAddedHandlerProto extends BaseHubEventHandlerProto {
    public ScenarioAddedHandlerProto(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public HubEventAvro toAvro(HubEventProto hubEvent) {
        ScenarioAddedEventProto scenarioAddedEvent = hubEvent.getScenarioAdded();

        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(mapTimestampToInstant(hubEvent))
                .setPayload(new ScenarioAddedEventAvro(scenarioAddedEvent.getName(),
                        mapToConditionTypeAvro(scenarioAddedEvent.getConditionList()),
                        mapToDeviceActionAvro(scenarioAddedEvent.getActionList())))
                .build();
    }

    private List<ScenarioConditionAvro> mapToConditionTypeAvro(List<ScenarioConditionProto> conditionList) {
        return conditionList.stream()
                .map(c -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(c.getSensorId())
                        .setType(
                                switch (c.getType()) {
                                    case MOTION -> ConditionTypeAvro.MOTION;
                                    case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
                                    case SWITCH -> ConditionTypeAvro.SWITCH;
                                    case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
                                    case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
                                    case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
                                    case UNRECOGNIZED -> null;
                                })
                        .setOperation(
                                switch (c.getOperation()) {
                                    case EQUALS -> ConditionOperationAvro.EQUALS;
                                    case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
                                    case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
                                    case UNRECOGNIZED -> null;
                                })
                        .setValue(
                                switch (c.getValueCase()) {
                                    case INT_VALUE -> c.getIntValue();
                                    case BOOL_VALUE -> c.getBoolValue() ? 1 : 0;
                                    case VALUE_NOT_SET -> null;
                                })
                        .build())
                .toList();
    }

    private List<DeviceActionAvro> mapToDeviceActionAvro(List<DeviceActionProto> actionList) {
        return actionList.stream()
                .map(da -> DeviceActionAvro.newBuilder()
                        .setSensorId(da.getSensorId())
                        .setType(
                                switch (da.getType()) {
                                    case ACTIVATE -> ActionTypeAvro.ACTIVATE;
                                    case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
                                    case INVERSE -> ActionTypeAvro.INVERSE;
                                    case SET_VALUE -> ActionTypeAvro.SET_VALUE;
                                    case UNRECOGNIZED -> null;
                                })
                        .setValue(da.getValue())
                        .build())
                .toList();
    }
}