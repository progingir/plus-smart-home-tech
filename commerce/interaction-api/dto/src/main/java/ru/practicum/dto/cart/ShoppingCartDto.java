package ru.practicum.dto.cart;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class ShoppingCartDto {
    @NotNull
    private UUID shoppingCartId;
    @NotNull
    private Map<UUID, Long> products;
}
