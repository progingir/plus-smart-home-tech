package ru.yandex.practicum.telemetry.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "conditions")
public class ScenarioCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long conditionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ConditionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation")
    private ConditionOperation operation;

    @Column(name = "value")
    private Integer value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id")
    @ToString.Exclude
    private Scenario scenario;

    @Column(name = "sensor_id")
    private String sensorId;


}
