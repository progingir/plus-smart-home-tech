package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.payment.PaymentDto;
import ru.practicum.model.Payment;
import ru.practicum.model.enums.PaymentState;

@UtilityClass
public class PaymentMapper {

    public Payment mapToPayment(OrderDto dto) {
        return Payment.builder()
                .totalPayment(dto.getTotalPrice())
                .deliveryTotal(dto.getDeliveryPrice())
                .productsTotal(dto.getProductPrice())
                .state(PaymentState.PENDING)
                .orderId(dto.getOrderId())
                .build();
    }

    public PaymentDto mapToDto(Payment payment, Double feeRatio) {
        return PaymentDto.builder()
                .deliveryTotal(payment.getDeliveryTotal())
                .feeTotal(payment.getProductsTotal() * feeRatio)
                .paymentId(payment.getPaymentId())
                .totalPayment(payment.getTotalPayment())
                .build();
    }
}
