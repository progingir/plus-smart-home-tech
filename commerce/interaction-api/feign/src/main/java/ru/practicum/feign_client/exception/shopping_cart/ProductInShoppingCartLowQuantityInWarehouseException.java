package ru.practicum.feign_client.exception.shopping_cart;

public class ProductInShoppingCartLowQuantityInWarehouseException extends RuntimeException {
    public ProductInShoppingCartLowQuantityInWarehouseException(String message) {
        super(message);
    }
}
