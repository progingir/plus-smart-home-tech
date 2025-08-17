package ru.practicum.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.dto.warehouse.AddressDto;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class CreateNewOrderRequest {
    @Valid
    ShoppingCartDto shoppingCart;
    @NotNull
    AddressDto deliveryAddress;
}
