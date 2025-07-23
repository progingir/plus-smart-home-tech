package ru.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenario_conditions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScenarioCondition {

    @EmbeddedId
    private ScenarioConditionId id;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("scenarioId")
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("sensorId")
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("conditionId")
    private Condition condition;
}

