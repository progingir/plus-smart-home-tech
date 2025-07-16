package ru.yandex.practicum.telemetry.analyzer.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "scenarioId")
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "scenarios")
public class Scenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scenarioId;

    @Column(name = "name")
    private String name;

    @Column(name = "hub_id")
    private String hubId;

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL)
    private Set<ScenarioCondition> scenarioConditions;

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL)
    private Set<Action> scenarioActions;


}
