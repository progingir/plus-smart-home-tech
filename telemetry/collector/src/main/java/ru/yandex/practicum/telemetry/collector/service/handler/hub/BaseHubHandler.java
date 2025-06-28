package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;


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
                        topic, null, System.currentTimeMillis(), hubEvent.getHubId(), toAvro(hubEvent));
        producer.sendRecord(record);
    }

    abstract SpecificRecordBase toAvro(HubEvent hubEvent);

}
