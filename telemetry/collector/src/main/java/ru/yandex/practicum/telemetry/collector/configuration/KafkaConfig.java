package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Data
@ConfigurationProperties("collector.kafka")
@Component
public class KafkaConfig {
    private Map<String, String> topics;
    private Properties producerProperties;
}