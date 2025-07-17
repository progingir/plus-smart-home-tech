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
        // Устанавливаем только необходимые свойства, без Schema Registry
        producerProperties.putIfAbsent("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.putIfAbsent("value.serializer", "kafka.serializer.GeneralAvroSerializer");
        return producerProperties;
    }
}