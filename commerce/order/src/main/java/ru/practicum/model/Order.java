package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.order.OrderState;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    UUID orderId;

    String owner;

    @Column(name = "shopping_cart_id")
    UUID shoppingCartId;

    @ElementCollection
    @CollectionTable(name = "order_products", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    Map<UUID, Long> products;

    @Column(name = "payment_id")
    UUID paymentId;

    @Column(name = "delivery_id")
    UUID deliveryId;

    @ManyToOne
    @JoinColumn(name = "delivery_address_id")
    Address deliveryAddress;

    @Enumerated(value = EnumType.STRING)
    OrderState state;

    @Column(name = "delivery_weight")
    Double deliveryWeight;
    @Column(name = "delivery_volume")
    Double deliveryVolume;
    Boolean fragile;

    @Column(name = "total_price")
    Double totalPrice;
    @Column(name = "delivery_price")
    Double deliveryPrice;
    @Column(name = "product_price")
    Double productPrice;

    LocalDateTime created;
}
