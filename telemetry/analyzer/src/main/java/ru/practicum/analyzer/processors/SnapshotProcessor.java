package ru.practicum.analyzer.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.handlers.snapshot.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final Consumer<String, SensorsSnapshotAvro> consumer;
    private final SnapshotHandler snapshotHandler;

    @Value("${topic.snapshots-topic}")
    private String topic;

    public void start() {
        consumer.subscribe(List.of(topic));

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records =
                        consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    SensorsSnapshotAvro snapshot = record.value();
                    log.info("Получили снимок состояния умного дома: {}", snapshot);

                    snapshotHandler.handleSnapshot(snapshot);
                }

                if (!records.isEmpty()) {
                    consumer.commitSync();
                }
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка чтения данных из топика {}", topic, e);
        } finally {
            try {
                consumer.close();
            } catch (Exception e) {
                log.warn("Ошибка при закрытии consumer’а", e);
            }
        }
    }
}
