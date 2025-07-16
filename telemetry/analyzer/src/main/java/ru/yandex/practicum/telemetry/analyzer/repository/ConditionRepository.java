package ru.yandex.practicum.telemetry.analyzer.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.telemetry.analyzer.entity.ScenarioCondition;

public interface ConditionRepository extends JpaRepository<ScenarioCondition, Long> {

}
