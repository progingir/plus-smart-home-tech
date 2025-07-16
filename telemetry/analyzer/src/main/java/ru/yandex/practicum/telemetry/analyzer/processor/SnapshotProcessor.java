package ru.yandex.practicum.telemetry.analyzer.processor;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.configuration.KafkaAnalyzerConfig;
import ru.yandex.practicum.telemetry.analyzer.service.SnapshotService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class SnapshotProcessor implements Runnable {

    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotConsumer;
    private final KafkaAnalyzerConfig kafkaConfig;
    private final SnapshotService snapshotService;

    public SnapshotProcessor(KafkaAnalyzerConfig kafkaConfig, SnapshotService snapshotService) {
        this.kafkaConfig = kafkaConfig;
        snapshotConsumer = new KafkaConsumer<>(kafkaConfig.getSnapshotConsumerProperties());
        this.snapshotService = snapshotService;
    }


    @Override
    public void run() {
        log.info("Starting snapshotProcessor");

        try (snapshotConsumer) {
            Runtime.getRuntime().addShutdownHook(new Thread(snapshotConsumer::wakeup));
            snapshotConsumer.subscribe(List.of(kafkaConfig.getTopics().get("sensors-snapshot")));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = snapshotConsumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro sensorsSnapshotAvro = record.value();
                    snapshotService.analyze(sensorsSnapshotAvro);
                }
                snapshotConsumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        log.warn("Commit processing error. Offsets: {}", offsets, exception);
                    }
                });
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Analyzer. Error by handling SnapshotEvents from kafka", e);
        } finally {
            log.info("Analyzer. Closing consumer.");
            snapshotConsumer.close();
        }
    }
}
