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
                Instant.now().toEpochMilli(), // Используем текущую временную метку
                event.getHubId(),
                toAvro(event)));
    }

    public abstract HubEventAvro toAvro(HubEventProto hubEvent);
}