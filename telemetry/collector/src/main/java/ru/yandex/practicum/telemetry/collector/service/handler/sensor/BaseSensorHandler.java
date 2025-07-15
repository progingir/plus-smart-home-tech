package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;

public abstract class BaseSensorHandler implements SensorEventHandler {

    protected final KafkaEventProducer producer;
    protected final String topic;

    public BaseSensorHandler(KafkaEventProducer kafkaProducer) {
        this.producer = kafkaProducer;
        this.topic = kafkaProducer.getConfig().getTopics().get("sensors-events");
    }

    @Override
    public void handle(SensorEvent sensorEvent) {
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(
                        topic,
                        null,
                        sensorEvent.getTimestamp().toEpochMilli(),
                        sensorEvent.getHubId(),
                        toSensorEventAvro(sensorEvent));
        producer.sendRecord(record);
    }

    private SensorEventAvro toSensorEventAvro(SensorEvent sensorEvent) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp().toEpochMilli())
                .setPayload(toAvro(sensorEvent))
                .build();
    }

    abstract SpecificRecordBase toAvro(SensorEvent sensorEvent);
}