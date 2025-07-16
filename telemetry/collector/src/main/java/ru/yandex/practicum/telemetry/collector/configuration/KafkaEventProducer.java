package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter @Setter @ToString
public class KafkaEventProducer {
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final KafkaConfig config;

    public KafkaEventProducer(KafkaConfig kafkaConfig) {
        this.config = kafkaConfig;
        this.producer = new KafkaProducer<>(kafkaConfig.getProducerProperties());
    }

    public void sendRecord(ProducerRecord<String, SpecificRecordBase> record) {
        log.info("Sending record to topic {}: {}", record.topic(), record.value());
        try {
            producer.send(record);
            producer.flush();
            log.info("Record sent successfully");
        } catch (Exception e) {
            log.error("Failed to send record to Kafka", e);
            throw new RuntimeException("Failed to send record to Kafka", e);
        }
    }

}
