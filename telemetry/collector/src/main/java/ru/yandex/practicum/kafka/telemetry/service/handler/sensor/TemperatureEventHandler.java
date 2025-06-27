package ru.yandex.practicum.kafka.telemetry.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro; // Исправлено
import ru.yandex.practicum.kafka.telemetry.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.model.sensor.SensorEvent;
import ru.yandex.practicum.kafka.telemetry.model.sensor.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.model.sensor.TemperatureSensorEvent;

@Component
public class TemperatureEventHandler extends BaseSensorHandler {

    public TemperatureEventHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    TemperatureSensorAvro toAvro(SensorEvent sensorEvent) { // Исправлено
        TemperatureSensorEvent temperatureEvent = (TemperatureSensorEvent) sensorEvent;

        return TemperatureSensorAvro.newBuilder()
                .setTemperatureF(temperatureEvent.getTemperatureF())
                .setTemperatureC(temperatureEvent.getTemperatureC()) // Исправлено
                .build();
    }
}
