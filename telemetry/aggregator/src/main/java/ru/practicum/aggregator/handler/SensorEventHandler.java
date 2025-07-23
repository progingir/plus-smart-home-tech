package ru.practicum.aggregator.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class SensorEventHandler {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        log.info("Processing SensorEventAvro for hubId: {}, sensorId: {}, timestamp: {}",
                hubId, event.getId(), event.getTimestamp());

        if (!snapshots.containsKey(hubId)) {
            SensorsSnapshotAvro snapshot = createNewSnapshot(event);
            snapshots.put(hubId, snapshot);
            log.info("Created new snapshot for hubId: {}, timestamp: {}", hubId, snapshot.getTimestamp());
            return Optional.of(snapshot);
        } else {
            SensorsSnapshotAvro oldSnapshot = snapshots.get(hubId);
            Optional<SensorsSnapshotAvro> updatedSnapshotOpt = updateSnapshot(oldSnapshot, event);
            updatedSnapshotOpt.ifPresent(sensorsSnapshotAvro -> {
                snapshots.put(hubId, sensorsSnapshotAvro);
                log.info("Updated snapshot for hubId: {}, timestamp: {}", hubId, sensorsSnapshotAvro.getTimestamp());
            });
            return updatedSnapshotOpt;
        }
    }

    private SensorsSnapshotAvro createNewSnapshot(SensorEventAvro event) {
        Map<String, SensorStateAvro> sensorStates = new HashMap<>();
        SensorStateAvro sensorState = createSensorState(event);
        sensorStates.put(event.getId(), sensorState);

        return SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp()) // Используем timestamp из события
                .setSensorsState(sensorStates)
                .build();
    }

    private Optional<SensorsSnapshotAvro> updateSnapshot(SensorsSnapshotAvro oldSnapshot, SensorEventAvro event) {
        String sensorId = event.getId();

        if (oldSnapshot.getSensorsState().containsKey(sensorId)) {
            if (oldSnapshot.getSensorsState().get(sensorId).getTimestamp() > event.getTimestamp() ||
                    oldSnapshot.getSensorsState().get(sensorId).getData().equals(event.getPayload())) {
                log.info("Skipping update for sensorId: {}, event timestamp: {} is older or data unchanged",
                        sensorId, event.getTimestamp());
                return Optional.empty();
            }
        }
        SensorStateAvro sensorState = createSensorState(event);
        oldSnapshot.getSensorsState().put(sensorId, sensorState);
        oldSnapshot.setTimestamp(event.getTimestamp());
        log.info("Updated sensor state for sensorId: {}, timestamp: {}", sensorId, event.getTimestamp());

        return Optional.of(oldSnapshot);
    }

    private SensorStateAvro createSensorState(SensorEventAvro event) {
        return SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
    }
}
