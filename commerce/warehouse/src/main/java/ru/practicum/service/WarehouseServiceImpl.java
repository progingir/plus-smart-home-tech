package ru.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.practicum.dto.warehouse.AddressDto;
import ru.practicum.dto.warehouse.BookedProductsDto;
import ru.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.practicum.enums.QuantityState;
import ru.practicum.feign_client.StoreClient;
import ru.practicum.feign_client.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.practicum.feign_client.exception.ProductNotFoundInWarehouseException;
import ru.practicum.address.AddressManager;
import ru.practicum.exceptions.SpecifiedProductAlreadyInWarehouseException;
import ru.practicum.mapper.WarehouseProductMapper;
import ru.practicum.model.WarehouseProduct;
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
    private final WarehouseRepository repository;
    private final StoreClient storeClient;

    @Override
    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest newProductRequest) {
        if (repository.existsById(newProductRequest.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Такой продукт уже есть на складе");
        }

        repository.save(WarehouseProductMapper.mapToProduct(newProductRequest));
    }

    @Override
    public BookedProductsDto checkProductsQuantity(ShoppingCartDto shoppingCartDto) {
        log.info("Начало проверки достаточного количества товаров {} на складе", shoppingCartDto.getProducts());
        return checkQuantity(shoppingCartDto);
    }

    @Override
    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest addProductQuantity) {
        log.info("Увеличиваем количество товара {} на складе на {}шт", addProductQuantity.getProductId(),
                addProductQuantity.getQuantity());

        WarehouseProduct product = repository.findById(addProductQuantity.getProductId())
                .orElseThrow(() -> new ProductNotFoundInWarehouseException("Такого товара нет на складе"));

        log.info("Сейчас товара на складе {}", product.getQuantity());
        product.setQuantity(product.getQuantity() + addProductQuantity.getQuantity());
        log.info("После обновления на складе {}шт", product.getQuantity());

        try {
            updateProductQuantityInShoppingStore(product);
        } catch (FeignException e) {
            if (e.status() == 404) {
                log.error("Товар ещё не добавили на витрину магазина");
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

    private BookedProductsDto checkQuantity(ShoppingCartDto shoppingCartDto) {
        Map<UUID, Long> cartProducts = shoppingCartDto.getProducts();
        Set<UUID> cartProductIds = cartProducts.keySet();

        Map<UUID, WarehouseProduct> warehouseProducts = repository.findAllById(cartProductIds).stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));

        Set<UUID> productIds = warehouseProducts.keySet();
        cartProductIds.forEach(id -> {
            if (!productIds.contains(id)) {
                throw new ProductNotFoundInWarehouseException(String.format("Товара с id = %s нет на складе", id));
            }
        });

        cartProducts.forEach((key, value) -> {
            if (warehouseProducts.get(key).getQuantity() < value) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException
                        (String.format("Товара с id = %s не хватает на складе", key));
            }
        });

        return getBookedProducts(warehouseProducts.values(), cartProducts);
    }

    private BookedProductsDto getBookedProducts(Collection<WarehouseProduct> productList,
                                                Map<UUID, Long> cartProducts) {
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
