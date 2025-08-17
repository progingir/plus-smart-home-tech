package ru.practicum.feign_client.exception.warehouse;

public class OrderBookingNotFoundException extends RuntimeException {
    public OrderBookingNotFoundException(String message) {
        super(message);
    }
}
