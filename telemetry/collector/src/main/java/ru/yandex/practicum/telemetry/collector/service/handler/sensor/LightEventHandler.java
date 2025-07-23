package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class LightEventHandler extends BaseSensorHandlerProto {
    public LightEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public SensorEventAvro toAvro(SensorEventProto sensorEvent) {
        LightSensorProto lightSensor = sensorEvent.getLightSensor();
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(mapTimestampToInstant(sensorEvent).toEpochMilli())
                .setPayload(LightSensorAvro.newBuilder()
                        .setLinkQuality(lightSensor.getLinkQuality())
                        .setLuminosity(lightSensor.getLuminosity())
                        .build())
                .build();
    }
}