package ru.practicum.feign_client.exception;

public class ProductNotFoundInWarehouseException extends RuntimeException {
    public ProductNotFoundInWarehouseException(String message) {
        super(message);
    }
}
