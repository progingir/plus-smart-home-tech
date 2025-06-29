package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;

@Component
public class MotionEventHandler extends BaseSensorHandler {

    public MotionEventHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

    @Override
    MotionSensorAvro toAvro(SensorEvent sensorEvent) {
        MotionSensorEvent motionEvent = (MotionSensorEvent) sensorEvent;

        return MotionSensorAvro.newBuilder()
                .setMotion(motionEvent.isMotion())
                .setLinkQuality(motionEvent.getLinkQuality())
                .setVoltage(motionEvent.getVoltage())
                .build();
    }

}
