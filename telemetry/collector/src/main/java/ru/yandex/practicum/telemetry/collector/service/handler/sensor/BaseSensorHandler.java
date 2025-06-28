package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;

import java.time.Instant;


public abstract class BaseSensorHandler implements SensorEventHandler {

    KafkaEventProducer producer;
    String topic;

    public BaseSensorHandler(KafkaEventProducer kafkaProducer) {
        this.producer = kafkaProducer;
        topic = kafkaProducer.getConfig().getTopics().get("sensors-events");
    }

    @Override
    public void handle(SensorEvent sensorEvent) {
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(
                        topic,
                        null,
                        System.currentTimeMillis(),
                        sensorEvent.getHubId(),
                        toSensorEventAvro(sensorEvent)); // Изменено на toSensorEventAvro
        producer.sendRecord(record);
    }

    private SensorEventAvro toSensorEventAvro(SensorEvent sensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(sensorEvent.getTimestamp().toEpochMilli()))
                .setPayload(toAvro(sensorEvent)) // toAvro возвращает специфичный Avro-объект
                .build();
    }

    abstract SpecificRecordBase toAvro(SensorEvent sensorEvent);

}

