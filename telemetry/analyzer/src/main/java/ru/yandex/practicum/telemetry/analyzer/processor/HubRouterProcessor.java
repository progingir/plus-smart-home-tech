package ru.yandex.practicum.telemetry.analyzer.processor;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.telemetry.analyzer.entity.Action;


@Slf4j
@Service
public class HubRouterProcessor {


    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public HubRouterProcessor(@Value("${grpc.client.hub-router.address}") String address) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(address)
                .usePlaintext()
                .build();
        this.hubRouterClient = HubRouterControllerGrpc.newBlockingStub(channel);
    }

    public void executeAction(Action action, String hubId) {
        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(action.getScenario().getName())
                .setAction(DeviceActionProto.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeProto.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build())
                .build();

        try{
            hubRouterClient.handleDeviceAction(request);
        } catch (StatusRuntimeException e) {
            log.error("Error. Cannot send DeviceActionRequest", e);
        }

    }

}
