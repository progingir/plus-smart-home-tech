package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaEventProducer;


public abstract class BaseHubHandler implements HubEventHandler {

    private static final Logger log = LoggerFactory.getLogger(BaseHubHandler.class);
    KafkaEventProducer producer;
    String topic;

    public BaseHubHandler(KafkaEventProducer kafkaProducer) {
        this.producer = kafkaProducer;
        topic = kafkaProducer.getConfig().getTopics().get("hubs-events");
    }

    @Override
    public void handle(HubEventProto hubEvent) {
        log.info("Handling Hub Event {}", hubEvent);
        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(
                        topic, null, System.currentTimeMillis(), hubEvent.getHubId(), toAvro(hubEvent));
        log.info("Sending Hub Event {}", record);
        producer.sendRecord(record);
    }

    abstract SpecificRecordBase toAvro(HubEventProto hubEvent);

}
