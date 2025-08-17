package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.payment.PaymentDto;
import ru.practicum.model.enums.PaymentState;
import ru.practicum.service.PaymentService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto makingPaymentForOrder(@Valid @RequestBody OrderDto orderDto) {
        log.info("Запрос на создание оплаты заказа {}", orderDto.getOrderId());
        return paymentService.makingPaymentForOrder(orderDto);
    }

    @PostMapping("/productCost")
    public Double calculateProductsCost(@Valid @RequestBody OrderDto orderDto) {
        log.info("Запрос на расчёт стоимости продуктов в заказе {}", orderDto.getOrderId());
        return paymentService.calculateProductsCost(orderDto);
    }

    @PostMapping("/totalCost")
    public Double calculateTotalCost(@Valid @RequestBody OrderDto orderDto) {
        log.info("Запрос на расчёт полной стоимости заказа {}", orderDto.getOrderId());
        return paymentService.calculateTotalCost(orderDto);
    }

    @PostMapping("/refund")
    public void changePaymentStateToSuccess(@RequestBody UUID paymentId) {
        log.info("Запрос на смену статуса оплаты {} на {}", paymentId, PaymentState.SUCCESS);
        paymentService.changePaymentStateToSuccess(paymentId);
    }

    @PostMapping("/failed")
    public void changePaymentStateToFailed(@RequestBody UUID paymentId) {
        log.info("Запрос на смену статуса оплаты {} на {}", paymentId, PaymentState.FAILED);
        paymentService.changePaymentStateToFailed(paymentId);
    }
}
