package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BaseHubEventHandlerProto implements HubEventHandlerProto {
    private final KafkaEventProducer producer;

    protected String topic() {
        return producer.getConfig().getTopics().get("hubs-events");
    }

    @Override
    public void handle(HubEventProto event) {
        producer.sendRecord(new org.apache.kafka.clients.producer.ProducerRecord<>(
                topic(),
                null,
                mapTimestampToInstant(event).toEpochMilli(),
                event.getHubId(),
                toAvro(event)));
    }

    public Instant mapTimestampToInstant(HubEventProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }

    public abstract HubEventAvro toAvro(HubEventProto hubEvent);
}