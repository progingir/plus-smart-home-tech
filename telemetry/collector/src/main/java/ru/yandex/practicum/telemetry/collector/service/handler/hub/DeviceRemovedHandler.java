package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaEventProducer;

@Component
public class DeviceRemovedHandler extends BaseHubHandler {

    private static final Logger log = LoggerFactory.getLogger(DeviceRemovedHandler.class);

    public DeviceRemovedHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    DeviceRemovedEventAvro toAvro(HubEventProto hubEvent) {
        log.info("Converting to Avro device removed event {}", hubEvent);
        DeviceRemovedEventProto removedDeviceEvent = hubEvent.getDeviceRemoved();

        return DeviceRemovedEventAvro.newBuilder()
                .setId(removedDeviceEvent.getId())
                .build();
    }

}
