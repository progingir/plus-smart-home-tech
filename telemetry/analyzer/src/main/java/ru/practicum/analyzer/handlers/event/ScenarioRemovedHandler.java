package ru.practicum.analyzer.handlers.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.repository.ScenarioActionRepository;
import ru.practicum.analyzer.repository.ScenarioConditionRepository;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioRemovedHandler implements HubEventHandler {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioRemovedEventAvro scenarioRemovedEvent = (ScenarioRemovedEventAvro) event.getPayload();

        Optional<Scenario> optScenario = scenarioRepository
                .findByHubIdAndName(event.getHubId(), scenarioRemovedEvent.getName());
        if (optScenario.isPresent()) {
            Scenario scenario = optScenario.get();
            scenarioActionRepository.deleteByScenario(scenario);
            scenarioConditionRepository.deleteByScenario(scenario);
            scenarioRepository.delete(scenario);
            log.info("Удаляем сценарий с name = {} из хаба с id = {}",
                    scenarioRemovedEvent.getName(),
                    event.getHubId());
        } else {
            log.info("Сценарий не найден");
        }

    }

    @Override
    public String getPayloadType() {
        return ScenarioRemovedEventAvro.class.getSimpleName();
    }
}
