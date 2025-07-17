package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Getter
@Setter
@ToString
public class KafkaEventProducer implements AutoCloseable, DisposableBean, ApplicationContextAware {
    private final KafkaProducer<String, SpecificRecordBase> producer;
    private final KafkaConfig config;
    private static ApplicationContext context;

    public KafkaEventProducer(KafkaConfig kafkaConfig) {
        this.config = kafkaConfig;
        Properties props = kafkaConfig.getProducerProperties();
        this.producer = new KafkaProducer<>(props);
    }

    public void sendRecord(ProducerRecord<String, SpecificRecordBase> record) {
        try {
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Ошибка при отправке записи в Kafka: " + exception.getMessage(), exception);
                }
            });
            producer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при отправке записи в Kafka: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        if (producer != null) {
            producer.close();
        }
    }

    @Override
    public void destroy() {
        close();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }
}