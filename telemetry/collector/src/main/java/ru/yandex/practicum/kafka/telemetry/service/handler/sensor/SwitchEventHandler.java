package ru.yandex.practicum.kafka.telemetry.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensor.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.model.sensor.SwitchSensorEvent;

@Component
public class SwitchEventHandler extends BaseSensorHandler {

    public SwitchEventHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    @Override
    SwitchSensorAvro toAvro(SensorEvent sensorEvent) {
        SwitchSensorEvent switchEvent = (SwitchSensorEvent) sensorEvent;

        return SwitchSensorAvro.newBuilder()
                .setState(switchEvent.isState())
                .build();
    }
}
