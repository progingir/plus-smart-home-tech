package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandlerProto {
    HubEventProto.PayloadCase getMessageType();

    void handle(HubEventProto event);
}