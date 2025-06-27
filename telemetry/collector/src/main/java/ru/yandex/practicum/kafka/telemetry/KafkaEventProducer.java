package ru.yandex.practicum.kafka.telemetry;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.config.KafkaConfig;

@Component
@Getter
@Setter
@ToString
public class KafkaEventProducer {
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final KafkaConfig config;

    public KafkaEventProducer(KafkaConfig kafkaConfig) {
        this.config = kafkaConfig;
        this.producer = new KafkaProducer<>(kafkaConfig.getProducerProperties());
    }

    public void sendRecord(ProducerRecord<String, SpecificRecordBase> record) {
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                throw new RuntimeException("Failed to send record to Kafka", exception);
            }
        });
        producer.flush(); // Вызываем flush только при необходимости
    }

    // Закрытие продюсера при завершении приложения
    public void close() {
        producer.close();
    }
}