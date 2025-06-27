package ru.yandex.practicum.kafka.telemetry.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensor.SensorEventType;

@Component
public class ClimateEventHandler extends BaseSensorHandler {

    public ClimateEventHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }


   ClimateSensorAvro toAvro(SensorEvent sensorEvent) {
        ClimateSensorEvent climateEvent = (ClimateSensorEvent) sensorEvent;

        return ClimateSensorAvro.newBuilder()
                .setCo2Level(climateEvent.getCo2Level())
                .setTemperatureC(climateEvent.getTemperatureC())
                .setHumidity(climateEvent.getHumidity())
                .build();
    }

}
