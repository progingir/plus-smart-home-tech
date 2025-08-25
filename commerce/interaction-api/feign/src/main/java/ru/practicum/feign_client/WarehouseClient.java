package ru.practicum.feign_client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.dto.warehouse.*;
import ru.practicum.feign_client.fallback.WarehouseClientFallback;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseClientFallback.class)
public interface WarehouseClient {
    @PutMapping
    void addNewProduct(@RequestBody NewProductInWarehouseRequest newProductRequest);

    @PostMapping("/check")
    BookedProductsDto checkProductsQuantity(@RequestBody ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void addProductQuantity(@RequestBody AddProductToWarehouseRequest addProductQuantity);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/assembly")
    BookedProductsDto assemblyProductsForOrder(@RequestBody AssemblyProductsForOrderRequest assemblyRequest);

    @PostMapping("/shipped")
    void shipProductsToDelivery(@RequestBody ShippedToDeliveryRequest request);

    @PostMapping("/return")
    void returnProducts(@RequestBody Map<UUID, Long> products);
}