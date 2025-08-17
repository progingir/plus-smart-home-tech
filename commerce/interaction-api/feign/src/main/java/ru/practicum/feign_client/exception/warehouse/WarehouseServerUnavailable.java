package ru.practicum.feign_client.exception.warehouse;

public class WarehouseServerUnavailable extends RuntimeException {
    public WarehouseServerUnavailable(String message) {
        super(message);
    }
}
