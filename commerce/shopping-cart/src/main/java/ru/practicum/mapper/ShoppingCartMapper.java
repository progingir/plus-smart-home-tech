package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.model.Cart;
import ru.practicum.dto.cart.ShoppingCartDto;

@UtilityClass
public class ShoppingCartMapper {

    public ShoppingCartDto mapToDto(Cart cart) {
        return ShoppingCartDto.builder()
                .shoppingCartId(cart.getShoppingCartId())
                .products(cart.getCartProducts())
                .build();
    }
}
