package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaEventProducer;

@Component
public class SwitchEventHandler extends BaseSensorHandler {

    private static final Logger log = LoggerFactory.getLogger(SwitchEventHandler.class);

    public SwitchEventHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }

    @Override
    SwitchSensorAvro toAvro(SensorEventProto sensorEvent) {
        log.info("Converting to Avro Switch sensor event: {}", sensorEvent);
        SwitchSensorEvent switchEvent = sensorEvent.getSwitchSensorEvent();

        return SwitchSensorAvro.newBuilder()
                .setState(switchEvent.getState())
                .build();
    }
}
