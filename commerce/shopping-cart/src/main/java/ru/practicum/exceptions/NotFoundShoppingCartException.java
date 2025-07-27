package ru.practicum.exceptions;

public class NotFoundShoppingCartException extends RuntimeException {
    public NotFoundShoppingCartException(String message) {
        super(message);
    }
}
