package ru.yandex.practicum.kafka.telemetry.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.model.hub.HubEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.ScenarioRemovedEvent;
import ru.yandex.practicum.kafka.telemetry.model.hub.enums.HubEventType;

@Component
public class ScenarioRemovedHandler extends BaseHubHandler {

    public ScenarioRemovedHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_REMOVED;
    }


    @Override
    ScenarioRemovedEventAvro toAvro(HubEvent hubEvent) {
        ScenarioRemovedEvent event = (ScenarioRemovedEvent) hubEvent;


        return ScenarioRemovedEventAvro.newBuilder()
                .setName(event.getName())
                .build();
    }
}
