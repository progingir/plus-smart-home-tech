package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.order.CreateNewOrderRequest;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.warehouse.BookedProductsDto;
import ru.practicum.enums.order.OrderState;
import ru.practicum.model.Address;
import ru.practicum.model.Order;

import java.time.LocalDateTime;

@UtilityClass
public class OrderMapper {

    public Order mapToOrder(CreateNewOrderRequest request, String username, BookedProductsDto bookedProducts,
                            Address address) {
        return Order.builder()
                .state(OrderState.NEW)
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .owner(username)
                .deliveryAddress(address)
                .created(LocalDateTime.now())
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .fragile(bookedProducts.getFragile())
                .build();
    }

    public OrderDto mapToDto(Order order) {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .deliveryId(order.getDeliveryId())
                .deliveryPrice(order.getDeliveryPrice())
                .deliveryVolume(order.getDeliveryVolume())
                .deliveryWeight(order.getDeliveryWeight())
                .fragile(order.getFragile())
                .paymentId(order.getPaymentId())
                .productPrice(order.getProductPrice())
                .products(order.getProducts())
                .totalPrice(order.getTotalPrice())
                .shoppingCartId(order.getShoppingCartId())
                .state(order.getState())
                .build();
    }
}
