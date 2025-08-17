package ru.practicum.service;

import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.payment.PaymentDto;

import java.util.UUID;

public interface PaymentService {
    PaymentDto makingPaymentForOrder(OrderDto orderDto);

    Double calculateProductsCost(OrderDto orderDto);

    Double calculateTotalCost(OrderDto orderDto);

    void changePaymentStateToSuccess(UUID paymentId);

    void changePaymentStateToFailed(UUID paymentId);
}
