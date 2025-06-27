package ru.yandex.practicum.kafka.telemetry.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.model.hub.DeviceRemovedEvent; // Исправлено
import ru.yandex.practicum.kafka.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.enums.HubEventType;

@Component
public class DeviceRemovedHandler extends BaseHubHandler {

    public DeviceRemovedHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_REMOVED;
    }

    @Override
    DeviceRemovedEventAvro toAvro(HubEvent hubEvent) {
        DeviceRemovedEvent removedDeviceEvent = (DeviceRemovedEvent) hubEvent; // Исправлено

        return DeviceRemovedEventAvro.newBuilder()
                .setId(removedDeviceEvent.getId())
                .build();
    }
}