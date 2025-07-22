package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseSensorHandlerProto implements SensorEventHandlerProto {
    private final KafkaEventProducer producer;
    @Value("${collector.kafka.topics.sensors-events}")
    private String topic;

    @Override
    public void handle(SensorEventProto event) {
        SensorEventAvro sensorEventAvro = toAvro(event);
        log.info("Send {}", sensorEventAvro);
        producer.sendRecord(new org.apache.kafka.clients.producer.ProducerRecord<>(
                topic,
                null,
                mapTimestampToInstant(event).toEpochMilli(),
                event.getHubId(),
                sensorEventAvro));
    }

    public abstract SensorEventAvro toAvro(SensorEventProto sensorEvent);

    public Instant mapTimestampToInstant(SensorEventProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }
}