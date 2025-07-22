package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BaseHubEventHandlerProto implements HubEventHandlerProto {
    private final KafkaEventProducer producer;
    @Value("${collector.kafka.topics.hubs-events}")
    private String topic;

    @Override
    public void handle(HubEventProto event) {
        producer.sendRecord(new org.apache.kafka.clients.producer.ProducerRecord<>(
                topic,
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