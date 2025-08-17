package ru.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.practicum.dto.cart.ShoppingCartDto;
import ru.practicum.exceptions.NoProductsInShoppingCartException;
import ru.practicum.exceptions.NotAuthorizedUserException;
import ru.practicum.exceptions.NotFoundShoppingCartException;
import ru.practicum.feign_client.WarehouseClient;
import ru.practicum.feign_client.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.practicum.feign_client.exception.ProductNotFoundInWarehouseException;
import ru.practicum.mapper.ShoppingCartMapper;
import ru.practicum.model.Cart;
import ru.practicum.model.enums.ShoppingCartState;
import ru.practicum.repository.ShoppingCartRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional
    public ShoppingCartDto addProductsInCart(String username, Map<UUID, Long> newProducts) {
        checkUsername(username);

        log.info("Начало добавления товаров в активную корзину");
        Optional<Cart> cartOpt = shoppingCartRepository.findByOwnerAndState(username, ShoppingCartState.ACTIVE);

        ShoppingCartDto shoppingCartDto;

        if (cartOpt.isPresent()) {
            Cart cart = cartOpt.get();
            log.info("Получили существующую корзину с товарами {}", cart.getCartProducts());
            cart.getCartProducts().putAll(newProducts);
            log.info("Добавили в неё новые товары и получили новый список товаров {}", cart.getCartProducts());
            shoppingCartDto = ShoppingCartMapper.mapToDto(cart);
        } else {
            shoppingCartDto = ShoppingCartMapper.mapToDto(shoppingCartRepository.save(Cart.builder()
                    .cartProducts(newProducts)
                    .state(ShoppingCartState.ACTIVE)
                    .owner(username)
                    .created(LocalDateTime.now())
                    .build()));
        }

        try {
            log.info("Проверка наличия товаров {} на складе", shoppingCartDto.getProducts());
            warehouseClient.checkProductsQuantity(shoppingCartDto);
            log.info("Проверка наличия товаров прошла успешно");
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ProductNotFoundInWarehouseException(e.getMessage());
            } else if (e.status() == 400) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(e.getMessage());
            } else {
                throw e;
            }
        }

        return shoppingCartDto;
    }

    @Override
    @Transactional
    public ShoppingCartDto getActiveShoppingCartOfUser(String username) {
        checkUsername(username);

        Optional<Cart> cartOpt = shoppingCartRepository.findByOwnerAndState(username, ShoppingCartState.ACTIVE);

        log.info("Возвращаем существующую активную корзину или создаём новую для пользователя {}", username);

        Cart result = cartOpt.orElseGet(() -> shoppingCartRepository.save(Cart.builder()
                .created(LocalDateTime.now())
                .owner(username)
                .state(ShoppingCartState.ACTIVE)
                .cartProducts(new HashMap<>())
                .build()));

        return ShoppingCartMapper.mapToDto(result);
    }

    @Override
    @Transactional
    public void deactivateCart(String username) {
        checkUsername(username);

        Cart cart = getActiveCartOfUser(username);

        log.info("получили корзину для деактивации: {}", cart.getShoppingCartId());

        cart.setState(ShoppingCartState.DEACTIVATE);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> productIds) {
        checkUsername(username);

        Cart cart = getActiveCartOfUser(username);

        Map<UUID, Long> oldProducts = cart.getCartProducts();


        if (!productIds.stream().allMatch(oldProducts::containsKey)) {
            throw new NoProductsInShoppingCartException("Таких продуктов нет в корзине");
        }

        Map<UUID, Long> newProducts = oldProducts.entrySet().stream()
                .filter(cp -> !productIds.contains(cp.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        cart.setCartProducts(newProducts);


        return ShoppingCartMapper.mapToDto(cart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        Cart cart = getActiveCartOfUser(username);

        Map<UUID, Long> products = cart.getCartProducts();

        if (!products.containsKey(request.getProductId())) {
            throw new NoProductsInShoppingCartException("Такого продукта нет в корзине");
        }

        products.put(request.getProductId(), request.getNewQuantity());

        return ShoppingCartMapper.mapToDto(cart);
    }

    private void checkUsername(String username) {
        if (username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }

    private Cart getActiveCartOfUser(String username) {
        return shoppingCartRepository.findByOwnerAndState(username, ShoppingCartState.ACTIVE)
                .orElseThrow(() -> new NotFoundShoppingCartException("У данного пользователя нет активной корзины"));
    }
}
