package ru.practicum.feign_client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.dto.store.ProductDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface CartClient {
    @PutMapping
    ShoppingCartDto addProductsInCart(@RequestParam String username,
                                      @RequestBody Map<UUID, Long> additionalProperties) throws FeignException;

    @GetMapping
    ShoppingCartDto getActiveShoppingCartOfUser(@RequestParam String username) throws FeignException;

    @DeleteMapping
    void deactivateCart(@RequestParam String username) throws FeignException;

    @PostMapping("/remove")
    ShoppingCartDto removeOtherProductsFromCart(@RequestParam String username,
                                                @RequestBody List<UUID> productIds) throws FeignException;

    @PostMapping("/change-quantity")
    ProductDto changeProductQuantity(@RequestParam String username,
                                     @RequestBody ChangeProductQuantityRequest request) throws FeignException;
}
