package ru.practicum.feign_client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.order.CreateNewOrderRequest;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.order.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {

    @PutMapping
    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest createOrderRequest,
                            @RequestParam String username) throws FeignException;

    @GetMapping
    List<OrderDto> getOrdersOfUser(@RequestParam String username,
                                   @RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) throws FeignException;

    @PostMapping("/return")
    OrderDto returnOrder(@RequestBody ProductReturnRequest returnRequest) throws FeignException;

    @PostMapping("/payment")
    OrderDto payOrder(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/payment/failed")
    OrderDto payOrderFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery")
    OrderDto sendOrderToDelivery(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/delivery/failed")
    OrderDto changeStateToDeliveryFailed(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/completed")
    OrderDto changeStateToCompleted(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/total")
    OrderDto calculateOrderTotalPrice(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/calculate/delivery")
    OrderDto calculateOrderDeliveryPrice(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly")
    OrderDto sendOrderToAssembly(@RequestBody UUID orderId) throws FeignException;

    @PostMapping("/assembly/failed")
    OrderDto changeOrderStateToAssemblyFailed(@RequestBody UUID orderId) throws FeignException;
}
