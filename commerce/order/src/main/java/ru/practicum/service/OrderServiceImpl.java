package ru.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.delivery.DeliveryDto;
import ru.practicum.dto.order.CreateNewOrderRequest;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.order.ProductReturnRequest;
import ru.practicum.dto.payment.PaymentDto;
import ru.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.practicum.dto.warehouse.BookedProductsDto;
import ru.practicum.enums.delivery.DeliveryState;
import ru.practicum.enums.order.OrderState;
import ru.practicum.exceptions.AuthorizationException;
import ru.practicum.exceptions.NoProductInOrderException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.feign_client.DeliveryClient;
import ru.practicum.feign_client.PaymentClient;
import ru.practicum.feign_client.WarehouseClient;
import ru.practicum.feign_client.exception.order.NoOrderFoundException;
import ru.practicum.feign_client.exception.payment.NotEnoughInfoInOrderToCalculateException;
import ru.practicum.feign_client.exception.shopping_cart.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.practicum.feign_client.exception.shopping_store.ProductNotFoundException;
import ru.practicum.feign_client.exception.warehouse.OrderBookingNotFoundException;
import ru.practicum.feign_client.exception.warehouse.ProductNotFoundInWarehouseException;
import ru.practicum.mapper.AddressMapper;
import ru.practicum.mapper.OrderMapper;
import ru.practicum.model.Address;
import ru.practicum.model.Order;
import ru.practicum.repository.AddressRepository;
import ru.practicum.repository.OrderRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest createOrderRequest, String username) {
        checkUsername(username);

        log.info("Создаём заказ для корзины с id = {}", createOrderRequest.getShoppingCart().getShoppingCartId());

        try {
            log.info("Проверяем наличие товаров на складе");
            BookedProductsDto bookedProducts = warehouseClient
                    .checkProductsQuantity(createOrderRequest.getShoppingCart());

            log.info("Сохраняем адрес доставки");
            Address deliveryAddress = addressRepository.save(AddressMapper
                    .mapToAddress(createOrderRequest.getDeliveryAddress()));

            log.info("Сохраняем информацию о заказе в БД");
            return OrderMapper.mapToDto(orderRepository.save(OrderMapper
                    .mapToOrder(createOrderRequest, username, bookedProducts, deliveryAddress)));
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ProductNotFoundInWarehouseException(e.getMessage());
            } else if (e.status() == 400) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(e.getMessage());
            } else {
                throw e;
            }
        }
    }

    @Override
    public List<OrderDto> getOrdersOfUser(String username, Integer page, Integer size) {
        checkUsername(username);

        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");

        PageRequest pageRequest = PageRequest.of(page, size, sortByCreated);

        return orderRepository.findAllByOwner(username, pageRequest).stream()
                .map(OrderMapper::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto returnOrder(ProductReturnRequest returnRequest) {
        if (returnRequest.getProducts().isEmpty()) {
            throw new ValidationException("Добавьте товары для возврата");
        }

        Order oldOrder = getOrder(returnRequest.getOrderId());

        if (oldOrder.getState() == OrderState.PRODUCT_RETURNED || oldOrder.getState() == OrderState.CANCELED) {
            throw new ValidationException("Заказ уже был возвращён или отменён");
        }

        if (oldOrder.getState() == OrderState.NEW) {
            throw new ValidationException("Заказ ещё не был собран на складе");
        }

        log.info("Возврат товаров {} из заказа orderId = {}", returnRequest.getProducts(), returnRequest.getOrderId());
        Map<UUID, Long> orderProducts = oldOrder.getProducts();
        Map<UUID, Long> returnProducts = returnRequest.getProducts();

        log.info("Проверка что все товары из заказа предоставлены к возврату");
        if (!orderProducts.keySet().stream()
                .filter(p -> !returnProducts.containsKey(p))
                .toList().isEmpty()) {
            throw new NoProductInOrderException("Не все продукты предоставлены для возврата");
        }
        log.info("Проверка все товары возвращаются в нужном количестве");
        orderProducts.forEach((key, value) -> {
            if (!Objects.equals(value, returnProducts.get(key))) {
                throw new ValidationException("Не совпадает количество товара с id = %s для возврата".formatted(key));
            }
        });

        try {
            log.info("Отправляем запрос на возвращение товаров на склад");
            warehouseClient.returnProducts(returnProducts);
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ProductNotFoundInWarehouseException(e.getMessage());
            } else {
                throw e;
            }
        }

        log.info("Меняем статус заказа на {}", OrderState.PRODUCT_RETURNED);
        oldOrder.setState(OrderState.PRODUCT_RETURNED);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto payOrder(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        if (oldOrder.getState() == OrderState.PAID) {
            log.info("заказ уже оплачен");
            return OrderMapper.mapToDto(oldOrder);
        }

        if (oldOrder.getState() == OrderState.ON_PAYMENT) {
            log.info("Успешная оплата заказа");
            oldOrder.setState(OrderState.PAID);
            return OrderMapper.mapToDto(oldOrder);
        }

        if (oldOrder.getState() != OrderState.ASSEMBLED) {
            throw new ValidationException("Заказ должен быть собран перед оплатой");
        }

        oldOrder.setState(OrderState.ON_PAYMENT);

        try {
            log.info("Отправляем заказ на оплату");
            PaymentDto paymentDto = paymentClient.makingPaymentForOrder(OrderMapper.mapToDto(oldOrder));
            oldOrder.setPaymentId(paymentDto.getPaymentId());
        } catch (FeignException e) {
            if (e.status() == 400) {
                throw new NotEnoughInfoInOrderToCalculateException(e.getMessage());
            } else {
                throw e;
            }
        }

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto changeStateToPaymentFailed(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        log.info("Меняем статус заказа на {}", OrderState.PAYMENT_FAILED);
        oldOrder.setState(OrderState.PAYMENT_FAILED);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto sendOrderToDelivery(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        if (oldOrder.getState() == OrderState.DELIVERED) {
            log.info("Заказ уже доставлен");
            return OrderMapper.mapToDto(oldOrder);
        }

        if (oldOrder.getState() == OrderState.ON_DELIVERY) {
            oldOrder.setState(OrderState.DELIVERED);
            return OrderMapper.mapToDto(oldOrder);
        }

        if (oldOrder.getState() != OrderState.PAID) {
            throw new ValidationException("Перед доставкой заказ должен быть оплачен");
        }

        try {
            log.info("Отправляем запрос на передачу заказа {} в доставку", oldOrder.getOrderId());
            deliveryClient.sendProductsToDelivery(oldOrder.getDeliveryId());
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new OrderBookingNotFoundException(e.getMessage());
            } else {
                throw e;
            }
        }

        log.info("Меняем статус заказа {} на {}", orderId, OrderState.ON_DELIVERY);
        oldOrder.setState(OrderState.ON_DELIVERY);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto changeStateToDeliveryFailed(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        log.info("Меняем статус заказа {} на {}", orderId, OrderState.DELIVERY_FAILED);
        oldOrder.setState(OrderState.DELIVERY_FAILED);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto changeStateToCompleted(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        log.info("Меняем статус заказа {} на {}", orderId, OrderState.COMPLETED);
        oldOrder.setState(OrderState.COMPLETED);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto calculateOrderTotalPrice(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        try {
            log.info("Отправляем запрос на расчёт стоимости продуктов в заказе");
            Double productsPrice = paymentClient.calculateProductsCost(OrderMapper.mapToDto(oldOrder));
            log.info("Получили стоимость продуктов {}", productsPrice);
            oldOrder.setProductPrice(productsPrice);

            log.info("Отправляем запрос на расчёт полной стоимости заказа");
            Double orderTotalPrice = paymentClient.calculateTotalCost(OrderMapper.mapToDto(oldOrder));
            log.info("Полная стоимость заказа = {}", orderTotalPrice);
            oldOrder.setTotalPrice(orderTotalPrice);
        } catch (FeignException e) {
            if (e.status() == 400) {
                throw new NotEnoughInfoInOrderToCalculateException(e.getMessage());
            } else if (e.status() == 404) {
                throw new ProductNotFoundException(e.getMessage());
            } else {
                throw e;
            }
        }

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto calculateOrderDeliveryPrice(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        try {
            log.info("Отправляем запрос на создание доставки для заказа {}", orderId);
            DeliveryDto deliveryDto = deliveryClient.createNewDelivery(DeliveryDto.builder()
                    .orderId(orderId)
                    .fromAddress(warehouseClient.getWarehouseAddress())
                    .toAddress(AddressMapper.mapToDto(oldOrder.getDeliveryAddress()))
                    .state(DeliveryState.CREATED)
                    .build());

            oldOrder.setDeliveryId(deliveryDto.getDeliveryId());

            log.info("Отправляем запрос на расчёт стоимости доставки заказа {}", orderId);
            Double deliveryPrice = deliveryClient.calculateDeliveryCost(OrderMapper.mapToDto(oldOrder));
            oldOrder.setDeliveryPrice(deliveryPrice);
        } catch (FeignException e) {
            if (e.status() == 400) {
                throw new ValidationException("Не задан id доставки");
            } else {
                throw e;
            }
        }

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto sendOrderToAssembly(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        if (oldOrder.getState() != OrderState.NEW) {
            throw new ValidationException("Нельзя отправить на сборку не новый заказ");
        }

        try {
            log.info("Отправляем запрос на сборку заказа {} на складе", orderId);
            warehouseClient.assemblyProductsForOrder(AssemblyProductsForOrderRequest.builder()
                    .orderId(oldOrder.getOrderId())
                    .products(oldOrder.getProducts())
                    .build());
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ProductNotFoundInWarehouseException(e.getMessage());
            } else if (e.status() == 400) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException(e.getMessage());
            } else {
                throw e;
            }
        }

        oldOrder.setState(OrderState.ASSEMBLED);

        return OrderMapper.mapToDto(oldOrder);
    }

    @Override
    @Transactional
    public OrderDto changeOrderStateToAssemblyFailed(UUID orderId) {
        Order oldOrder = getOrder(orderId);

        log.info("Меняем статус заказа на {}", OrderState.ASSEMBLY_FAILED);
        oldOrder.setState(OrderState.ASSEMBLY_FAILED);

        return OrderMapper.mapToDto(oldOrder);
    }

    private void checkUsername(String username) {
        if (username.isBlank()) {
            throw new AuthorizationException("Имя пользователя не должно быть пустым");
        }
    }

    private Order getOrder(UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> new NoOrderFoundException("Заказ не найден"));
    }
}
