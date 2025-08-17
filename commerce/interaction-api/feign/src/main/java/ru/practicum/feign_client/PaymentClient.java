package ru.practicum.feign_client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.payment.PaymentDto;

import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient {

    @PostMapping
    PaymentDto makingPaymentForOrder(@RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/productCost")
    Double calculateProductsCost(@RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/totalCost")
    Double calculateTotalCost(@RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/refund")
    void changePaymentStateToSuccess(@RequestBody UUID paymentId) throws FeignException;

    @PostMapping("/failed")
    void changePaymentStateToFailed(@RequestBody UUID paymentId) throws FeignException;
}
