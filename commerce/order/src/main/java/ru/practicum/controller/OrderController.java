package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.order.CreateNewOrderRequest;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.order.ProductReturnRequest;
import ru.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    @PutMapping
    public OrderDto createNewOrder(@Valid @RequestBody CreateNewOrderRequest createOrderRequest,
                                   @RequestParam String username) {
        log.info("Запрос на добавление нового заказа {}", createOrderRequest);
        return orderService.createNewOrder(createOrderRequest, username);
    }

    @GetMapping
    public List<OrderDto> getOrdersOfUser(@RequestParam String username,
                                          @RequestParam(defaultValue = "0") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на получение заказов пользователя");
        return orderService.getOrdersOfUser(username, page, size);
    }

    @PostMapping("/return")
    public OrderDto returnOrder(@Valid @RequestBody ProductReturnRequest returnRequest) {
        log.info("Запрос на возврат товаров из заказа orderId = {}", returnRequest.getOrderId());
        return orderService.returnOrder(returnRequest);
    }

    @PostMapping("/payment")
    public OrderDto payOrder(@RequestBody UUID orderId) {
        log.info("Запрос на оплату заказа orderId = {}", orderId);
        return orderService.payOrder(orderId);
    }

    @PostMapping("/payment/failed")
    public OrderDto payOrderFailed(@RequestBody UUID orderId) {
        log.info("Неудачная оплата заказа");
        return orderService.changeStateToPaymentFailed(orderId);
    }

    @PostMapping("/delivery")
    public OrderDto sendOrderToDelivery(@RequestBody UUID orderId) {
        log.info("Запрос на передачу заказа {} в доставку", orderId);
        return orderService.sendOrderToDelivery(orderId);
    }

    @PostMapping("/delivery/failed")
    public OrderDto changeStateToDeliveryFailed(@RequestBody UUID orderId) {
        log.info("Сообщение о том, что заказ не был доставлен");
        return orderService.changeStateToDeliveryFailed(orderId);
    }

    @PostMapping("/completed")
    public OrderDto changeStateToCompleted(@RequestBody UUID orderId) {
        log.info("Запрос на завершение заказа {}", orderId);
        return orderService.changeStateToCompleted(orderId);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateOrderTotalPrice(@RequestBody UUID orderId) {
        log.info("Запрос на расчёт полной стоимости заказа");
        return orderService.calculateOrderTotalPrice(orderId);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateOrderDeliveryPrice(@RequestBody UUID orderId) {
        log.info("Запрос на расчёт стоимости доставки заказа");
        return orderService.calculateOrderDeliveryPrice(orderId);
    }

    @PostMapping("/assembly")
    public OrderDto sendOrderToAssembly(@RequestBody UUID orderId) {
        log.info("Запрос на сборку заказа на складе");
        return orderService.sendOrderToAssembly(orderId);
    }

    @PostMapping("/assembly/failed")
    public OrderDto changeOrderStateToAssemblyFailed(@RequestBody UUID orderId) {
        log.info("Не удалось собрать заказ {}", orderId);
        return orderService.changeOrderStateToAssemblyFailed(orderId);
    }
}
