package ru.yandex.practicum.telemetry.analyzer.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

@Service
public class ScenarioService {

    private ScenarioRepository scenarioRepository;

    public void add(Scenario scenario) {
        scenarioRepository.save(scenario);
    }

    public void delete(String name) {
        scenarioRepository.deleteByName(name);
    }

}
