package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;

@Component
public class LightEventHandler extends BaseSensorHandler {

    private static final Logger log = LoggerFactory.getLogger(LightEventHandler.class);

    public LightEventHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    LightSensorAvro toAvro(SensorEventProto sensorEvent) {
        log.info("Converting to Avro LightSensorEvent: {}", sensorEvent);
        LightSensorEvent lightEvent = sensorEvent.getLightSensorEvent();

        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightEvent.getLinkQuality())
                .setLuminosity(lightEvent.getLuminosity())
                .build();
    }
}
