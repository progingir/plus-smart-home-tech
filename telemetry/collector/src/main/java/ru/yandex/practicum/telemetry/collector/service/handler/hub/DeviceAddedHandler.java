package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.telemetry.collector.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.model.hub.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.DeviceType;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.HubEventType;

@Component
public class DeviceAddedHandler extends BaseHubHandler {

    public DeviceAddedHandler(KafkaEventProducer kafkaProducer) {
        super(kafkaProducer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }

    @Override
    SpecificRecordBase toAvro(HubEvent hubEvent) {
        DeviceAddedEvent addedDeviceEvent = (DeviceAddedEvent) hubEvent;
        return DeviceAddedEventAvro.newBuilder()
                .setId(addedDeviceEvent.getId())
                .setType(toDeviceTypeAvro(addedDeviceEvent.getDeviceType()))
                .build();
    }

    private DeviceTypeAvro toDeviceTypeAvro(DeviceType deviceType) {
        return switch (deviceType) {
            case MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
        };
    }
}