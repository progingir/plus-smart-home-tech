package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.delivery.DeliveryState;

import java.util.UUID;

@Entity
@Table(name = "delivery")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID deliveryId;

    @Column(name = "order_id")
    UUID orderId;

    @Enumerated(value = EnumType.STRING)
    DeliveryState state;

    @ManyToOne
    @JoinColumn(name = "from_address_id")
    Address fromAddress;

    @ManyToOne
    @JoinColumn(name = "to_address_id")
    Address toAddress;

    @Column(name = "delivery_weight")
    Double deliveryWeight;
    @Column(name = "delivery_volume")
    Double deliveryVolume;
    Boolean fragile;
}
