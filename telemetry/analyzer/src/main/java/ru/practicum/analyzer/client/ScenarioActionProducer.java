package ru.practicum.analyzer.client;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.model.ScenarioAction;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

import java.time.Instant;

@Slf4j
@Service
public class ScenarioActionProducer {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub;

    public ScenarioActionProducer(
            @GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterStub) {
        this.hubRouterStub = hubRouterStub;
    }

    public void sendAction(ScenarioAction scenarioAction) {
        DeviceActionRequest actionRequest = mapToActionRequest(scenarioAction);

        hubRouterStub.handleDeviceAction(actionRequest);
        log.info("Действие {} отправлено в hub-router", actionRequest);
    }

    private DeviceActionRequest mapToActionRequest(ScenarioAction scenarioAction) {
        var scenario   = scenarioAction.getScenario();
        var sensor     = scenarioAction.getSensor();
        var actionBody = scenarioAction.getAction();

        return DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(DeviceActionProto.newBuilder()
                        .setSensorId(sensor.getId())
                        .setType(mapActionType(actionBody.getType()))
                        .setValue(actionBody.getValue())
                        .build())
                .setTimestamp(currentTimestamp())
                .build();
    }

    private ActionTypeProto mapActionType(ActionTypeAvro actionType) {
        return switch (actionType) {
            case ACTIVATE   -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE    -> ActionTypeProto.INVERSE;
            case SET_VALUE  -> ActionTypeProto.SET_VALUE;
        };
    }

    private Timestamp currentTimestamp() {
        Instant instant = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
