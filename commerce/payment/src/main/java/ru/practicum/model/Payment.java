package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.enums.PaymentState;

import java.util.UUID;

@Entity
@Table(name = "payments")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID paymentId;

    @Column(name = "order_id")
    UUID orderId;

    @Column(name = "total_payment")
    Double totalPayment;

    @Column(name = "delivery_total")
    Double deliveryTotal;

    @Column(name = "products_total")
    Double productsTotal;

    @Enumerated(value = EnumType.STRING)
    PaymentState state;
}
