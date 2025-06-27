package ru.yandex.practicum.kafka.telemetry.service.handler.sensor;

import ru.yandex.practicum.kafka.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensor.SensorEventType;

public interface SensorEventHandler {

    SensorEventType getMessageType();

    void handle(SensorEvent sensorEvent);

}
