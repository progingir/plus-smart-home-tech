package ru.practicum.exceptions;

public class NoPaymentFoundException extends RuntimeException {
    public NoPaymentFoundException(String message) {
        super(message);
    }
}
