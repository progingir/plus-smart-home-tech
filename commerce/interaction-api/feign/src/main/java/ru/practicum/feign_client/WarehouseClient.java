package ru.practicum.feign_client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.practicum.dto.warehouse.AddressDto;
import ru.practicum.dto.warehouse.BookedProductsDto;
import ru.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.practicum.feign_client.fallback.WarehouseClientFallback;

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
}
