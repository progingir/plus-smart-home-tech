package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandlerProto {
    SensorEventProto.PayloadCase getMessageType();
    void handle(SensorEventProto event);
}
