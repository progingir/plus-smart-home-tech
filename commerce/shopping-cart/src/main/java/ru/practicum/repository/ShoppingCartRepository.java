package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Cart;
import ru.practicum.model.enums.ShoppingCartState;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByOwnerAndState(String owner, ShoppingCartState state);
}
