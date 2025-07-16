package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaEventProducer;

@Component
public class ScenarioRemovedHandler extends BaseHubHandler {

    private static final Logger log = LoggerFactory.getLogger(ScenarioRemovedHandler.class);

    public ScenarioRemovedHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }


    @Override
    ScenarioRemovedEventAvro toAvro(HubEventProto hubEvent) {
        log.info("Converting to Avro ScenarioRemovedEvent: {}", hubEvent);
        ScenarioRemovedEventProto event = hubEvent.getScenarioRemoved();

        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }
}
