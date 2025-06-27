package ru.yandex.practicum.kafka.telemetry.service.handler.hub;

import ru.yandex.practicum.kafka.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.enums.HubEventType;


public interface HubEventHandler {

    HubEventType getMessageType();

    void handle(HubEvent hubEvent);

}
