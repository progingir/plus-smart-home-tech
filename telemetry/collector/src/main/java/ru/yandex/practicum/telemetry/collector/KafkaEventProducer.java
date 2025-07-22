package ru.yandex.practicum.telemetry.collector;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaConfig;

import java.util.Properties;

@Slf4j
@Component
@Getter
@Setter
@ToString
public class KafkaEventProducer {
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final KafkaConfig config;

    public KafkaEventProducer(KafkaConfig kafkaConfig) {
        this.config = kafkaConfig;
        Properties props = new Properties();
        if (kafkaConfig.getProducerProperties() != null && !kafkaConfig.getProducerProperties().isEmpty()) {
            log.info("Kafka producer properties: {}", kafkaConfig.getProducerProperties());
            props.putAll(kafkaConfig.getProducerProperties());
        } else {
            log.error("Kafka producer properties are missing or empty");
            throw new IllegalStateException("Kafka producer properties are missing or empty");
        }
        log.info("Initializing KafkaProducer with properties: {}", props);
        this.producer = new KafkaProducer<>(props);
    }

    public void sendRecord(ProducerRecord<String, SpecificRecordBase> record) {
        log.info("Sending record to Kafka: {}", record);
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to send record to Kafka", exception);
                throw new RuntimeException("Failed to send record to Kafka", exception);
            }
            log.info("Record sent successfully: {}", metadata);
        });
        producer.flush();
    }
}