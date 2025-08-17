package ru.practicum.exceptions;

public class NoProductInOrderException extends RuntimeException {
    public NoProductInOrderException(String message) {
        super(message);
    }
}
