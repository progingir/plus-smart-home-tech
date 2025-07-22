package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorHandlerProto implements SensorEventHandlerProto {
    private final KafkaEventProducer producer;

    protected String topic() {
        return producer.getConfig().getTopics().get("sensors-events");
    }

    @Override
    public void handle(SensorEventProto event) {
        SensorEventAvro sensorEventAvro = toAvro(event);
        log.info("Send {}", sensorEventAvro);
        producer.sendRecord(new org.apache.kafka.clients.producer.ProducerRecord<>(
                topic(),
                null,
                mapTimestampToInstant(event).toEpochMilli(), // Используем timestamp из gRPC-сообщения
                event.getHubId(),
                sensorEventAvro));
    }

    protected Instant mapTimestampToInstant(SensorEventProto event) {
        if (!event.hasTimestamp() || event.getTimestamp().getSeconds() <= 0) {
            log.warn("Invalid or missing timestamp in SensorEventProto, using current time");
            return Instant.now();
        }
        Instant timestamp = Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
        // Проверка на разумность даты (не позже 2030 года)
        if (timestamp.isAfter(Instant.parse("2030-01-01T00:00:00Z"))) {
            log.warn("Timestamp too far in future: {}, using current time", timestamp);
            return Instant.now();
        }
        return timestamp;
    }

    public abstract SensorEventAvro toAvro(SensorEventProto sensorEvent);
}