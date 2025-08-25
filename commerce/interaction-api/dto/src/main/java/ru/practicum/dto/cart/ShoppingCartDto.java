package ru.practicum.dto.cart;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotEmpty
    private Map<@NotNull UUID, @NotNull @Positive Long> products;
}
