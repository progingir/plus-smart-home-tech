package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaEventProducer;

@Component
public class DeviceAddedHandler extends BaseHubHandler {

    private static final Logger log = LoggerFactory.getLogger(DeviceAddedHandler.class);

    public DeviceAddedHandler(KafkaEventProducer kafkaProducer) {
        super(kafkaProducer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    DeviceAddedEventAvro toAvro(HubEventProto hubEvent) {
        log.info("Converting to Avro device added event {}", hubEvent);
        DeviceAddedEventProto addedDeviceEvent = hubEvent.getDeviceAdded();

        return DeviceAddedEventAvro.newBuilder()
                .setId(addedDeviceEvent.getId())
                .setType(toDeviceTypeAvro(addedDeviceEvent.getType()))

                .build();
    }

    private DeviceTypeAvro toDeviceTypeAvro(DeviceTypeProto deviceType) {
        return switch (deviceType) {
            case DeviceTypeProto.MOTION_SENSOR -> DeviceTypeAvro.MOTION_SENSOR;
            case DeviceTypeProto.CLIMATE_SENSOR -> DeviceTypeAvro.CLIMATE_SENSOR;
            case DeviceTypeProto.LIGHT_SENSOR -> DeviceTypeAvro.LIGHT_SENSOR;
            case DeviceTypeProto.SWITCH_SENSOR -> DeviceTypeAvro.SWITCH_SENSOR;
            case DeviceTypeProto.TEMPERATURE_SENSOR -> DeviceTypeAvro.TEMPERATURE_SENSOR;
            default -> null;
        };
    }

}
