package ru.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scenario_actions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScenarioAction {

    @EmbeddedId
    private ScenarioActionId id;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("scenarioId")
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("sensorId")
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("actionId")
    private Action action;
}

