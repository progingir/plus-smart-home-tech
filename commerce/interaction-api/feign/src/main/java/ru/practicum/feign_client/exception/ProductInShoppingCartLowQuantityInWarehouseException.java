package ru.practicum.feign_client.exception;

public class ProductInShoppingCartLowQuantityInWarehouseException extends RuntimeException {
    public ProductInShoppingCartLowQuantityInWarehouseException(String message) {
        super(message);
    }
}
