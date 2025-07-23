package ru.practicum.analyzer.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfiguration {
    private final Environment env;

    @Bean
    public Consumer<String, HubEventAvro> getHubEventConsumer() {
        Properties config = new Properties();

        config.put(ConsumerConfig.CLIENT_ID_CONFIG, env.getProperty("spring.kafka.consumer.hub-client-id"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("spring.kafka.consumer.hub-group-id"));
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.bootstrap-servers"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                env.getProperty("spring.kafka.consumer.key-deserializer"));
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                env.getProperty("spring.kafka.consumer.hub-event-deserializer"));
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                env.getProperty("spring.kafka.consumer.enable-auto-commit"));

        return new KafkaConsumer<>(config);
    }

    @Bean
    public Consumer<String, SensorsSnapshotAvro> getSnapsotConsumer() {
        Properties config = new Properties();

        config.put(ConsumerConfig.CLIENT_ID_CONFIG, env.getProperty("spring.kafka.consumer.snapshots-client-id"));
        config.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("spring.kafka.consumer.snapshot-group-id"));
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.bootstrap-servers"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                env.getProperty("spring.kafka.consumer.key-deserializer"));
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                env.getProperty("spring.kafka.consumer.snapshot-deserializer"));
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                env.getProperty("spring.kafka.consumer.enable-auto-commit"));

        return new KafkaConsumer<>(config);
    }
}
