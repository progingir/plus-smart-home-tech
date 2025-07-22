package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class ClimateEventHandler extends BaseSensorHandlerProto {
    public ClimateEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public SensorEventAvro toAvro(SensorEventProto sensorEvent) {
        ClimateSensorProto climateSensor = sensorEvent.getClimateSensor();
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(mapTimestampToInstant(sensorEvent).toEpochMilli()) // Используем timestamp из gRPC
                .setPayload(ClimateSensorAvro.newBuilder()
                        .setTemperatureC(climateSensor.getTemperatureC())
                        .setHumidity(climateSensor.getHumidity())
                        .setCo2Level(climateSensor.getCo2Level())
                        .build())
                .build();
    }
}
