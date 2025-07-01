package ru.yandex.practicum.telemetry.collector.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.enums.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.collector.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.handler.sensor.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class EventController {

    private final Map<HubEventType, HubEventHandler> hubEventHandlerMap;
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlerMap;

    public EventController(Set<HubEventHandler> hubEventHandlers, Set<SensorEventHandler> sensorEventHandlers) {
        this.hubEventHandlerMap = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
        this.sensorEventHandlerMap = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
    }

    @PostMapping("/sensors")
    public void addSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        var handler = sensorEventHandlerMap.get(sensorEvent.getType());
        if (handler == null) {
            throw new IllegalArgumentException("No handler for sensor event type: " + sensorEvent.getType());
        }
        handler.handle(sensorEvent);
    }

    @PostMapping("/hubs")
    public void addHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        var handler = hubEventHandlerMap.get(hubEvent.getType());
        if (handler == null) {
            throw new IllegalArgumentException("No handler for hub event type: " + hubEvent.getType());
        }
        handler.handle(hubEvent);
    }
}