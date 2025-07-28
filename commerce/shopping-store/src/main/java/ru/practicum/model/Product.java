package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.enums.ProductCategory;
import ru.practicum.enums.ProductState;
import ru.practicum.enums.QuantityState;

import java.util.UUID;

@Entity
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {
    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID productId;

    @Column(name = "product_name")
    String productName;

    String description;

    @Column(name = "image_src")
    String imageSrc;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "quantity_state")
    QuantityState quantityState;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "product_state")
    ProductState productState;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "product_category")
    ProductCategory productCategory;

    Float price;
}
