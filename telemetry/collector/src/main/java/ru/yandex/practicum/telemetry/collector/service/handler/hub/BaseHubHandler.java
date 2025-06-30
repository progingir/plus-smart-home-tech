package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;

public abstract class BaseHubHandler implements HubEventHandler {

    protected final KafkaEventProducer producer;
    protected final String topic;

    public BaseHubHandler(KafkaEventProducer kafkaProducer) {
        this.producer = kafkaProducer;
        this.topic = kafkaProducer.getConfig().getTopics().get("hubs-events");
    }

    @Override
    public void handle(HubEvent hubEvent) {
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(
                        topic,
                        null,
                        hubEvent.getTimestamp().toEpochMilli(),
                        hubEvent.getHubId(),
                        toHubEventAvro(hubEvent));
        producer.sendRecord(record);
    }

    private HubEventAvro toHubEventAvro(HubEvent hubEvent) {
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp().toEpochMilli())
                .setPayload(toAvro(hubEvent))
                .build();
    }

    abstract SpecificRecordBase toAvro(HubEvent hubEvent);
}