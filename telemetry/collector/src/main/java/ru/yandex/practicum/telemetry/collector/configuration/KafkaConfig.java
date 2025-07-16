package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Getter @Setter @ToString
@ConfigurationProperties("collector.kafka")
@Component
public class KafkaConfig {

    private Map<String, String> topics;
    private Properties producerProperties;

    public Properties getProducerProperties() {
        if (producerProperties == null) {
            producerProperties = new Properties();
        }
        // Убедимся, что есть настройки сериализации Avro
        producerProperties.putIfAbsent("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.putIfAbsent("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        producerProperties.putIfAbsent("schema.registry.url", "http://localhost:8081");
        return producerProperties;
    }
}
