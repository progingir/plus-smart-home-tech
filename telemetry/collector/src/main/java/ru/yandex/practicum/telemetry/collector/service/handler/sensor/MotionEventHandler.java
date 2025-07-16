package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaEventProducer;

@Component
public class MotionEventHandler extends BaseSensorHandler {

    private static final Logger log = LoggerFactory.getLogger(MotionEventHandler.class);

    public MotionEventHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }

    @Override
    MotionSensorAvro toAvro(SensorEventProto sensorEvent) {
        log.info("Converting to Avro Motion sensor event: {}", sensorEvent);
        MotionSensorEvent motionEvent = sensorEvent.getMotionSensorEvent();

        return MotionSensorAvro.newBuilder()
                .setMotion(motionEvent.getMotion())
                .setLinkQuality(motionEvent.getLinkQuality())
                .setVoltage(motionEvent.getVoltage())
                .build();
    }

}
