package ru.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.address.AddressManager;
import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.dto.warehouse.*;
import ru.practicum.enums.store.QuantityState;
import ru.practicum.exceptions.SpecifiedProductAlreadyInWarehouseException;
import ru.practicum.feign_client.StoreClient;
import ru.practicum.feign_client.exception.shopping_cart.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.practicum.feign_client.exception.warehouse.OrderBookingNotFoundException;
import ru.practicum.feign_client.exception.warehouse.ProductNotFoundInWarehouseException;
import ru.practicum.mapper.WarehouseProductMapper;
import ru.practicum.model.OrderBooking;
import ru.practicum.model.WarehouseProduct;
import ru.practicum.repository.OrderBookingRepository;
import ru.practicum.repository.WarehouseRepository;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final OrderBookingRepository orderBookingRepository;
    private final StoreClient storeClient;

    @Override
    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest newProductRequest) {
        if (warehouseRepository.existsById(newProductRequest.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Такой продукт уже есть на складе");
        }

        warehouseRepository.save(WarehouseProductMapper.mapToProduct(newProductRequest));
    }

    @Override
    public BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto) {
        log.info("Начало проверки достаточного количества товаров {} на складе", shoppingCartDto.getProducts());

        Map<UUID, WarehouseProduct> warehouseProducts = warehouseRepository
                .findAllById(shoppingCartDto.getProducts().keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        return checkQuantity(shoppingCartDto.getProducts(), warehouseProducts);
    }

    @Override
    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest addProductQuantity) {
        log.info("Увеличиваем количество товара {} на складе на {}шт", addProductQuantity.getProductId(),
                addProductQuantity.getQuantity());

        WarehouseProduct product = warehouseRepository.findById(addProductQuantity.getProductId())
                .orElseThrow(() -> new ProductNotFoundInWarehouseException("Такого товара нет на складе"));

        log.info("Сечас товара на складе {}", product.getQuantity());
        product.setQuantity(product.getQuantity() + addProductQuantity.getQuantity());
        log.info("После обновления на складе {}шт", product.getQuantity());

        try {
            updateProductQuantityInShoppingStore(product);
        } catch (FeignException e) {
            if (e.status() == 404) {
                log.info("Товар ещё не добавили на витрину магазина");
            } else {
                log.error("Ошибка при обновлении количества товара в магазине", e);
            }
        }
    }

    @Override
    public AddressDto getWarehouseAddress() {
        String address = AddressManager.CURRENT_ADDRESS;
        return AddressDto.builder()
                .country(address)
                .city(address)
                .street(address)
                .house(address)
                .flat(address)
                .build();
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest assemblyRequest) {
        Map<UUID, Long> assemblyProducts = assemblyRequest.getProducts();

        Map<UUID, WarehouseProduct> warehouseProducts = warehouseRepository
                .findAllById(assemblyProducts.keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        BookedProductsDto bookedProducts = checkQuantity(assemblyProducts, warehouseProducts);

        warehouseProducts.forEach((key, value) -> value.setQuantity(value.getQuantity() - assemblyProducts.get(key)));

        orderBookingRepository.save(OrderBooking.builder()
                .products(assemblyProducts)
                .orderId(assemblyRequest.getOrderId())
                .build());

        return bookedProducts;
    }

    @Override
    @Transactional
    public void shipProductsToDelivery(ShippedToDeliveryRequest request) {
        OrderBooking orderBooking = orderBookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new OrderBookingNotFoundException("Для данного заказа не найдено бронирование"));

        log.info("Передаём заказ {} в доставку", request.getOrderId());

        orderBooking.setDeliveryId(request.getDeliveryId());
    }

    @Override
    @Transactional
    public void returnProducts(Map<UUID, Long> products) {
        Map<UUID, WarehouseProduct> warehouseProducts = warehouseRepository.findAllById(products.keySet()).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        products.forEach((key, value) -> {
            if (!warehouseProducts.containsKey(key)) {
                throw new ProductNotFoundInWarehouseException("Товара с id = %s нет на складе".formatted(key));
            }
            WarehouseProduct warehouseProduct = warehouseProducts.get(key);
            warehouseProduct.setQuantity(warehouseProduct.getQuantity() + value);
        });
    }

    private BookedProductsDto checkQuantity(Map<UUID, Long> cartProducts,
                                            Map<UUID, WarehouseProduct> warehouseProducts) {
        Set<UUID> productIds = warehouseProducts.keySet();
        cartProducts.keySet().forEach(id -> {
            if (!productIds.contains(id)) {
                throw new ProductNotFoundInWarehouseException(String.format("Товара с id = %s нет на складе", id));
            }
        });
        log.info("Прошли проверку что такие товары есть на складе");

        cartProducts.forEach((key, value) -> {
            if (warehouseProducts.get(key).getQuantity() < value) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException
                        (String.format("Товара с id = %s не хватает на складе", key));
            }
        });
        log.info("Прошли проверку что товаров хватает на складе");

        return getBookedProducts(warehouseProducts.values(), cartProducts);
    }

    private BookedProductsDto getBookedProducts(Collection<WarehouseProduct> productList,
                                                Map<UUID, Long> cartProducts) {
        log.info("Рассчитываем параметры заказа");
        return BookedProductsDto.builder()
                .fragile(productList.stream().anyMatch(WarehouseProduct::getFragile))
                .deliveryWeight(productList.stream()
                        .mapToDouble(p -> p.getWeight() * cartProducts.get(p.getProductId()))
                        .sum())
                .deliveryVolume(productList.stream()
                        .mapToDouble(p ->
                                p.getWidth() * p.getHeight() * p.getDepth() * cartProducts.get(p.getProductId()))
                        .sum())
                .build();
    }

    private void updateProductQuantityInShoppingStore(WarehouseProduct product) {
        UUID productId = product.getProductId();
        QuantityState quantityState;
        Long quantity = product.getQuantity();

        if (quantity == 0) {
            quantityState = QuantityState.ENDED;
        } else if (quantity < 10) {
            quantityState = QuantityState.ENOUGH;
        } else if (quantity < 100) {
            quantityState = QuantityState.FEW;
        } else {
            quantityState = QuantityState.MANY;
        }

        log.info("Обновляем quantity_state на {} для товара с id = {}", quantityState, productId);
        storeClient.setQuantityState(productId, quantityState);
    }
}
