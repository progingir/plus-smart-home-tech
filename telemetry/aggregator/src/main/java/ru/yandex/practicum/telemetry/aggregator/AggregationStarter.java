package ru.yandex.practicum.telemetry.aggregator;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.configuration.KafkaConfig;
import ru.yandex.practicum.telemetry.aggregator.repository.SnapshotsRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class AggregationStarter {

    private final KafkaProducer<String, SensorsSnapshotAvro> producer;
    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final KafkaConfig kafkaConfig;
    private final SnapshotsRepository snapshotsRepository;

    public AggregationStarter(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
        producer = new KafkaProducer<>(kafkaConfig.getProducerProperties());
        consumer = new KafkaConsumer<>(kafkaConfig.getConsumerProperties());
        snapshotsRepository = new SnapshotsRepository();
    }

    public void start() {
        try(consumer) {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(kafkaConfig.getTopics().get("sensors-events")));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records =
                        consumer.poll(Duration.ofSeconds(5));
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro event = record.value();
                    Optional<SensorsSnapshotAvro> updateSnapshot = updateState(event);
                    updateSnapshot.ifPresent(this::sendSnapshot);
                }

                consumer.commitAsync((offsets, exception) -> {
                    if(exception != null) {
                        log.warn("Commit processing error. Offsets: {}", offsets, exception);
                    }
                });
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Aggregator. Error by handling events from sensors", e);
        } finally {
            log.info("Aggregator. Closing consumer.");
            consumer.close();
            log.info("Aggregator. Closing producer.");
            producer.close();
        }
    }

    private Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        Optional<SensorsSnapshotAvro> oldSnapshot = snapshotsRepository.get(event.getHubId());

        if (oldSnapshot.isPresent()) {
            Optional<SensorStateAvro> oldEvent =
                    Optional.ofNullable(oldSnapshot.get().getSensorsState().get(event.getId()));
            if (oldEvent.isPresent() && oldEvent.get().getTimestamp().isBefore(Instant.ofEpochMilli(event.getTimestamp()))) {
                SensorsSnapshotAvro newSnapshot = oldSnapshot.get();
                newSnapshot.setTimestamp(Instant.ofEpochMilli(event.getTimestamp()));
                SensorStateAvro newSensorState = new SensorStateAvro();
                newSensorState.setTimestamp(Instant.ofEpochMilli(event.getTimestamp()));
                newSensorState.setData(event.getPayload()); // Используем payload
                newSnapshot.getSensorsState().put(event.getId(), newSensorState);
                return Optional.of(newSnapshot);
            } else {
                return Optional.empty();
            }
        } else {
            Map<String, SensorStateAvro> state = new HashMap<>();
            SensorStateAvro sensorStateAvro = new SensorStateAvro();
            sensorStateAvro.setTimestamp(Instant.ofEpochMilli(event.getTimestamp()));
            sensorStateAvro.setData(event.getPayload()); // Используем payload
            state.put(event.getId(), sensorStateAvro);
            return Optional.of(snapshotsRepository.update(event.getHubId(),
                    SensorsSnapshotAvro.newBuilder()
                            .setHubId(event.getHubId())
                            .setTimestamp(Instant.now())
                            .setSensorsState(state)
                            .build()));
        }
    }

    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        log.info("Sending snapshot for {}. SnapShot: {}", snapshot.getHubId(), snapshot);
        ProducerRecord<String, SensorsSnapshotAvro> snapshotRecord =
                new ProducerRecord<>(
                        kafkaConfig.getTopics().get("sensors-snapshots"), null, snapshot.getHubId(), snapshot);
        log.info("Sending snapshot {}", snapshotRecord);
        try {
            producer.send(snapshotRecord);
            producer.flush();
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}