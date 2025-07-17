package ru.yandex.practicum.telemetry.analyzer.configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Getter @Setter @ToString
@ConfigurationProperties("analyzer.kafka")
@Component
public class KafkaAnalyzerConfig {

    private Properties hubConsumerProperties;
    private Properties snapshotConsumerProperties;
    private Map<String, String> topics;

    public Properties getHubConsumerProperties() {
        if (hubConsumerProperties == null) {
            hubConsumerProperties = new Properties();
        }
        hubConsumerProperties.putIfAbsent("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        hubConsumerProperties.putIfAbsent("value.deserializer", "kafka.serializer.HubEventDeserializer");
        return hubConsumerProperties;
    }

    public Properties getSnapshotConsumerProperties() {
        if (snapshotConsumerProperties == null) {
            snapshotConsumerProperties = new Properties();
        }
        snapshotConsumerProperties.putIfAbsent("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        snapshotConsumerProperties.putIfAbsent("value.deserializer", "kafka.serializer.SensorSnapshotDeserializer");
        return snapshotConsumerProperties;
    }
}