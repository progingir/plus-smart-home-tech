package ru.yandex.practicum.telemetry.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.telemetry.collector.service.handler.hub.HubEventHandlerProto;
import ru.yandex.practicum.telemetry.collector.service.handler.sensor.SensorEventHandlerProto;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
@Slf4j
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, SensorEventHandlerProto> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandlerProto> hubEventHandlers;

    public EventController(Set<SensorEventHandlerProto> sensorEventHandlers,
                           Set<HubEventHandlerProto> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandlerProto::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandlerProto::getMessageType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        SensorEventProto.PayloadCase sensorEventType = request.getPayloadCase();
        log.info("Получили сообщение датчика {}", request);
        try {
            if (sensorEventHandlers.containsKey(sensorEventType)) {
                log.info("Отправляем сообщение на обработку");
                sensorEventHandlers.get(sensorEventType).handle(request);
            } else {
                throw new IllegalArgumentException("Не могу найти обработчик для события " + sensorEventType);
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        HubEventProto.PayloadCase hubEventType = request.getPayloadCase();
        log.info("Получили сообщение хаба типа: {}", hubEventType);
        try {
            if (hubEventHandlers.containsKey(hubEventType)) {
                log.info("Отправляем сообщение на обработку");
                hubEventHandlers.get(hubEventType).handle(request);
            } else {
                throw new IllegalArgumentException("Не могу найти обработчик для события " + hubEventType);
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}