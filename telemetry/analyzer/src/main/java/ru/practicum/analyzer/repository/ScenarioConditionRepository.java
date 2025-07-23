package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.model.ScenarioCondition;
import ru.practicum.analyzer.model.ScenarioConditionId;

import java.util.List;

public interface ScenarioConditionRepository
        extends JpaRepository<ScenarioCondition, ScenarioConditionId> {

    void deleteByScenario(Scenario scenario);

    List<ScenarioCondition> findByScenario(Scenario scenario);
}

