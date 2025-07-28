package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.ShoppingCartService;
import ru.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.practicum.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PutMapping
    public ShoppingCartDto addProductsInCart(@RequestParam String username, @RequestBody Map<UUID, Long> newProducts) {
        log.info("Получили: username = {}; newProducts = {}", username, newProducts);

        return shoppingCartService.addProductsInCart(username, newProducts);
    }

    @GetMapping
    public ShoppingCartDto getActiveShoppingCartOfUser(@RequestParam String username) {
        log.info("Запрос на получение активной корзины пользователя {}", username);
        return shoppingCartService.getActiveShoppingCartOfUser(username);
    }

    @DeleteMapping
    public void deactivateCart(@RequestParam String username) {
        log.info("Запрос на деактивацию корзины товаров");
        shoppingCartService.deactivateCart(username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProductsFromCart(@RequestParam String username, @RequestBody List<UUID> productIds) {
        log.info("Запрос на удаление продуктов {} из корзины", productIds);
        return shoppingCartService.removeProductsFromCart(username, productIds);
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam String username,
                                                 @Valid @RequestBody ChangeProductQuantityRequest request) {
        log.info("Запрос на изменение количества товара в корзине");
        return shoppingCartService.changeProductQuantity(username, request);
    }
}
