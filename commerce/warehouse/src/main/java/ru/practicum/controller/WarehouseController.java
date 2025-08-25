package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.dto.warehouse.*;
import ru.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService service;

    @PutMapping
    public void addNewProduct(@Valid @RequestBody NewProductInWarehouseRequest newProductRequest) {
        log.info("Запрос на добавление нового товара на склад");
        service.addNewProduct(newProductRequest);
    }

    @PostMapping("/check")
    public BookedProductsDto checkProductsQuantity(@Valid @RequestBody ShoppingCartDto shoppingCartDto) {
        log.info("Поступил запрос на проверку наличия товаров из корзины");
        return service.checkProductsQuantity(shoppingCartDto);
    }

    @PostMapping("/add")
    public void addProductQuantity(@Valid @RequestBody AddProductToWarehouseRequest addProductQuantity) {
        log.info("Запрос на добавление товара с id = {} в количестве {}шт", addProductQuantity.getProductId(),
                addProductQuantity.getQuantity());
        service.addProductQuantity(addProductQuantity);
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("Запрос на получение адреса склада");
        return service.getWarehouseAddress();
    }

    @PostMapping("/assembly")
    public BookedProductsDto assemblyProductsForOrder(
            @Valid @RequestBody AssemblyProductsForOrderRequest assemblyRequest) {
        log.info("Получили запрос на сборку заказа {} с продуктами {}", assemblyRequest.getOrderId(),
                assemblyRequest.getProducts());
        return service.assemblyProductsForOrder(assemblyRequest);
    }

    @PostMapping("/shipped")
    public void shipProductsToDelivery(@Valid @RequestBody ShippedToDeliveryRequest request) {
        log.info("Запрос на передачу в доставку заказа {}", request.getOrderId());
        service.shipProductsToDelivery(request);
    }

    @PostMapping("/return")
    public void returnProducts(@RequestBody Map<UUID, Long> products) {
        log.info("Запрос на возврат продуктов {} на склад", products);
        service.returnProducts(products);
    }
}
