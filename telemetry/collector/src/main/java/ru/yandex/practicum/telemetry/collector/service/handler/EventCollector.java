package ru.yandex.practicum.telemetry.collector.service.handler;


import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.telemetry.collector.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.handler.sensor.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@GrpcService
public class EventCollector extends CollectorControllerGrpc.CollectorControllerImplBase {

    private static final Logger log = LoggerFactory.getLogger(EventCollector.class);
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlerMap;
    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlerMap;

    public EventCollector(Set<HubEventHandler> hubEventHandlerSet,
                           Set<SensorEventHandler> sensorEventHandlerSet) {
        this.hubEventHandlerMap = hubEventHandlerSet.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
        this.sensorEventHandlerMap = sensorEventHandlerSet.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
    }

    @Override
    public void collectHubEvent (HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received Hub event: {}", request);
            hubEventHandlerMap.get(request.getPayloadCase()).handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectSensorEvent (SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Received Sensor event: {}", request);
            sensorEventHandlerMap.get(request.getPayloadCase()).handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }


}
