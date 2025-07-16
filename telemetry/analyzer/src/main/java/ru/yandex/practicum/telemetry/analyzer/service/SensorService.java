package ru.yandex.practicum.telemetry.analyzer.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.analyzer.entity.Sensor;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;

    public void add(Sensor sensor) {
        sensorRepository.save(sensor);
    }

    public void delete(String hubId, String sensorId) {
        sensorRepository.deleteByHubIdAndSensorId(hubId, sensorId);
    }


}
