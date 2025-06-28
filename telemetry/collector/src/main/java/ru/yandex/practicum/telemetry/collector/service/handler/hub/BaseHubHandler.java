package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;

import java.time.Instant;


public abstract class BaseHubHandler implements HubEventHandler {

    KafkaEventProducer producer;
    String topic;

    public BaseHubHandler(KafkaEventProducer kafkaProducer) {
        this.producer = kafkaProducer;
        topic = kafkaProducer.getConfig().getTopics().get("hubs-events");
    }

    @Override
    public void handle(HubEvent hubEvent) {
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(
                        topic,
                        null,
                        System.currentTimeMillis(),
                        hubEvent.getHubId(),
                        toHubEventAvro(hubEvent)); // Изменено на toHubEventAvro
        producer.sendRecord(record);
    }

    private HubEventAvro toHubEventAvro(HubEvent hubEvent) {
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(Instant.ofEpochSecond(hubEvent.getTimestamp().toEpochMilli()))
                .setPayload(toAvro(hubEvent)) // toAvro возвращает специфичный Avro-объект
                .build();
    }

    abstract SpecificRecordBase toAvro(HubEvent hubEvent);

}
