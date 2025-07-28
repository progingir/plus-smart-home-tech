package ru.practicum.feign_client.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.practicum.dto.warehouse.AddressDto;
import ru.practicum.dto.warehouse.BookedProductsDto;
import ru.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.practicum.feign_client.WarehouseClient;
import ru.practicum.feign_client.exception.WarehouseServerUnavailable;

@Component
public class WarehouseClientFallback implements WarehouseClient {

    @Override
    public void addNewProduct(NewProductInWarehouseRequest newProductRequest) {
        throw new WarehouseServerUnavailable("Сервер warehouse временно недоступен");
    }

    @Override
    public BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto) {
        throw new WarehouseServerUnavailable("Сервер warehouse временно недоступен");
    }

    @Override
    public void addProductQuantity(AddProductToWarehouseRequest addProductQuantity) {
        throw new WarehouseServerUnavailable("Сервер warehouse временно недоступен");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        throw new WarehouseServerUnavailable("Сервер warehouse временно недоступен");
    }
}
