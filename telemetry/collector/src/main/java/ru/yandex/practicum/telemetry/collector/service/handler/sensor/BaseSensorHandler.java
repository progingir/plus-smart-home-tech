package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaEventProducer;

public abstract class BaseSensorHandler implements SensorEventHandler {

    private static final Logger log = LoggerFactory.getLogger(BaseSensorHandler.class);
    KafkaEventProducer producer;
    String topic;

    public BaseSensorHandler(KafkaEventProducer kafkaProducer) {
        this.producer = kafkaProducer;
        topic = kafkaProducer.getConfig().getTopics().get("sensors-events");
    }

    // Абстрактный метод для получения специфических данных датчика
    abstract SpecificRecordBase getPayload(SensorEventProto sensorEvent);

    @Override
    public void handle(SensorEventProto sensorEvent) {
        log.info("Received sensor event {}", sensorEvent);
        // Получаем специфические данные датчика
        SpecificRecordBase payload = getPayload(sensorEvent);
        // Создаем SensorEventAvro с общими полями и специфическим payload
        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(payload)
                .build();
        ProducerRecord<String, SensorEventAvro> record =
                new ProducerRecord<>(
                        topic, null, System.currentTimeMillis(), sensorEvent.getHubId(), eventAvro);
        log.info("Sending sensor event {}", record);
        producer.sendRecord(record);
    }
}