package ru.practicum.aggregator.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.handler.SensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorStarter {
    private final Consumer<String, SpecificRecordBase> consumer;
    private final SensorEventHandler eventHandler;
    private final Producer<String, SpecificRecordBase> producer;
    @Value("${aggregator.topic.telemetry-snapshots}")
    private String snapshotsTopic;
    @Value("${topic.telemetry-sensors}")
    private String sensorsTopic;

    public void start() {
        try {
            consumer.subscribe(List.of(sensorsTopic));
            log.info("Subscribed to topic: {}", sensorsTopic);

            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    log.info("Processing message: hubId={}, timestamp={}", record.key(), record.timestamp());
                    SensorEventAvro event = (SensorEventAvro) record.value();
                    log.info("Received SensorEventAvro: hubId={}, sensorId={}, timestamp={}",
                            event.getHubId(), event.getId(), event.getTimestamp());
                    Optional<SensorsSnapshotAvro> snapshot = eventHandler.updateState(event);
                    log.info("Generated snapshot: {}", snapshot);
                    if (snapshot.isPresent()) {
                        log.info("Sending snapshot to topic: {}, hubId: {}, timestamp: {}",
                                snapshotsTopic, event.getHubId(), event.getTimestamp());
                        ProducerRecord<String, SpecificRecordBase> message = new ProducerRecord<>(
                                snapshotsTopic,
                                null,
                                event.getTimestamp(),
                                event.getHubId(),
                                snapshot.get());
                        producer.send(message);
                    }
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
            log.info("Consumer wakeup triggered, shutting down...");
        } catch (Exception e) {
            log.error("Error processing sensor events", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Closing consumer");
                consumer.close();
                log.info("Closing producer");
                producer.close(Duration.ofSeconds(10));
            }
        }
    }
}