package ru.practicum.feign_client.exception.shopping_cart;

public class NotFoundShoppingCartException extends RuntimeException {
    public NotFoundShoppingCartException(String message) {
        super(message);
    }
}
