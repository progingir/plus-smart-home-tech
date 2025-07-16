package ru.yandex.practicum.telemetry.analyzer.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.entity.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, String> {

    void deleteByHubIdAndSensorId(final String hubId, final String id);

}
