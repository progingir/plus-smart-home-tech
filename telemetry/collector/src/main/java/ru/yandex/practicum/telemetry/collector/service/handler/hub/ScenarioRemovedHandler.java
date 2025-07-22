package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;

@Component
public class ScenarioRemovedHandler extends BaseHubEventHandlerProto {
    public ScenarioRemovedHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public HubEventAvro toAvro(HubEventProto hubEvent) {
        ScenarioRemovedEventProto scenarioRemovedEvent = hubEvent.getScenarioRemoved();
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(mapTimestampToInstant(hubEvent).toEpochMilli()) // Используем timestamp из gRPC
                .setPayload(new ScenarioRemovedEventAvro(scenarioRemovedEvent.getName()))
                .build();
    }
}