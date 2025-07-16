package ru.yandex.practicum.telemetry.analyzer.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "actionId")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "actions")
public class Action {

    @Id
    private Long actionId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ActionType type;

    @Column(name = "value")
    private Integer value;

    @Column(name = "sensor_id")
    private String sensorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    @ToString.Exclude
    private Scenario scenario;

}
