package ru.practicum.analyzer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioConditionId implements Serializable {
    @Column(name = "scenario_id")   private Long scenarioId;
    @Column(name = "sensor_id")     private String sensorId;
    @Column(name = "condition_id")  private Long conditionId;
}

