package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.model.ScenarioAction;
import ru.practicum.analyzer.model.ScenarioActionId;

import java.util.List;

public interface ScenarioActionRepository
        extends JpaRepository<ScenarioAction, ScenarioActionId> {

    void deleteByScenario(Scenario scenario);

    List<ScenarioAction> findByScenario(Scenario scenario);
}

