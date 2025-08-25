package ru.practicum.service;

import ru.practicum.dto.order.CreateNewOrderRequest;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto createNewOrder(CreateNewOrderRequest createOrderRequest, String username);

    List<OrderDto> getOrdersOfUser(String username, Integer page, Integer size);

    OrderDto returnOrder(ProductReturnRequest returnRequest);

    OrderDto payOrder(UUID orderId);

    OrderDto changeStateToPaymentFailed(UUID orderId);

    OrderDto sendOrderToDelivery(UUID orderId);

    OrderDto changeStateToDeliveryFailed(UUID orderId);

    OrderDto changeStateToCompleted(UUID orderId);

    OrderDto calculateOrderTotalPrice(UUID orderId);

    OrderDto calculateOrderDeliveryPrice(UUID orderId);

    OrderDto sendOrderToAssembly(UUID orderId);

    OrderDto changeOrderStateToAssemblyFailed(UUID orderId);
}
