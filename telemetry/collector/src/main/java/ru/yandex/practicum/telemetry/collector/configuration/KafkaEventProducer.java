package ru.yandex.practicum.telemetry.collector.configuration;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

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
        try(producer) {
            producer.send(record);
            producer.flush();
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
