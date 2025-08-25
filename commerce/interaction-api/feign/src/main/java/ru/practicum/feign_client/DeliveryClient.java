package ru.practicum.feign_client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.delivery.DeliveryDto;
import ru.practicum.dto.order.OrderDto;

import java.util.UUID;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryClient {

    @PutMapping
    DeliveryDto createNewDelivery(@RequestBody DeliveryDto deliveryDto);

    @PostMapping("/cost")
    Double calculateDeliveryCost(@RequestBody OrderDto orderDto);

    @PostMapping("/picked")
    void sendProductsToDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/successful")
    void changeStateToDelivered(@RequestBody UUID deliveryId);

    @PostMapping("/failed")
    void changeStateToFailed(@RequestBody UUID deliveryId);
}
