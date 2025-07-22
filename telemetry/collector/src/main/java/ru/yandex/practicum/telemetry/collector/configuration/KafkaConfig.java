package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@ConfigurationProperties(prefix = "topic")
@Component
public class KafkaConfig {
    private Map<String, String> topics = new HashMap<>();
    private Map<String, String> producerProperties = new HashMap<>();

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    public void init() {
        producerProperties.put("bootstrap.servers", bootstrapServers);
        producerProperties.put("key.serializer", keySerializer);
        producerProperties.put("value.serializer", valueSerializer);
        log.info("KafkaConfig initialized - Topics: {}, ProducerProperties: {}", topics, producerProperties);
    }
}