package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.delivery.DeliveryDto;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.service.DeliveryService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController {
    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto createNewDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        log.info("Запрос на создание новой доставки для заказа orderId = {}", deliveryDto.getOrderId());
        return deliveryService.createNewDelivery(deliveryDto);
    }

    @PostMapping("/cost")
    public Double calculateDeliveryCost(@Valid @RequestBody OrderDto orderDto) {
        log.info("Запрос на расчёт стоимости доставки");
        return deliveryService.calculateDeliveryCost(orderDto);
    }

    @PostMapping("/picked")
    public void sendProductsToDelivery(@RequestBody UUID deliveryId) {
        log.info("Запрос на передачу товара в доставку");
        deliveryService.sendProductsToDelivery(deliveryId);
    }

    @PostMapping("/successful")
    public void changeStateToDelivered(@RequestBody UUID deliveryId) {
        log.info("Запрос на смену статуса доставки на DELIVERED");
        deliveryService.changeStateToDelivered(deliveryId);
    }

    @PostMapping("/failed")
    public void changeStateToFailed(@RequestBody UUID deliveryId) {
        log.info("Запрос на смену статуса доставки на FAILED");
        deliveryService.changeStateToFailed(deliveryId);
    }
}
