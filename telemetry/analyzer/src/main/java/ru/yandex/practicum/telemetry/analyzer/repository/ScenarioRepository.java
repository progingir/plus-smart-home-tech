package ru.yandex.practicum.telemetry.analyzer.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.entity.Scenario;

import java.util.Set;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    public void deleteByName(String scenarioName);

    Set<Scenario> findByHubId(String hubId);

}
