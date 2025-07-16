package ru.yandex.practicum.telemetry.analyzer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = {"sensorId", "hubId"})
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "sensors")
public class Sensor {

    @Id
    private String sensorId;

    @Column(name = "hub_id")
    private String hubId;

}
