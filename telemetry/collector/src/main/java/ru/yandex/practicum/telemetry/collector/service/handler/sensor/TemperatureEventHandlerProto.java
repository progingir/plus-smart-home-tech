package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;

@Component
public class TemperatureEventHandlerProto extends BaseSensorHandlerProto {
    public TemperatureEventHandlerProto(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public SensorEventAvro toAvro(SensorEventProto sensorEvent) {
        TemperatureSensorProto temperatureSensor = sensorEvent.getTemperatureSensor();

        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(mapTimestampToInstant(sensorEvent))
                .setPayload(TemperatureSensorAvro.newBuilder()
                        .setTemperatureC(temperatureSensor.getTemperatureC())
                        .setTemperatureF(temperatureSensor.getTemperatureF())
                        .build())
                .build();
    }
}