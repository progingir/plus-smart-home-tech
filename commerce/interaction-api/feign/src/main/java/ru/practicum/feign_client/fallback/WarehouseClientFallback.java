package ru.practicum.feign_client.fallback;

import org.springframework.stereotype.Component;
import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.dto.warehouse.*;
import ru.practicum.feign_client.WarehouseClient;
import ru.practicum.feign_client.exception.warehouse.WarehouseServerUnavailable;

import java.util.Map;
import java.util.UUID;

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

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest assemblyRequest) {
        throw new WarehouseServerUnavailable("Сервер warehouse временно недоступен");
    }

    @Override
    public void shipProductsToDelivery(ShippedToDeliveryRequest request) {
        throw new WarehouseServerUnavailable("Сервер warehouse временно недоступен");
    }

    @Override
    public void returnProducts(Map<UUID, Long> products) {
        throw new WarehouseServerUnavailable("Сервер warehouse временно недоступен");
    }
}
