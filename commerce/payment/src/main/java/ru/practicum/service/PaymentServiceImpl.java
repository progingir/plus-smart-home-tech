package ru.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.payment.PaymentDto;
import ru.practicum.dto.store.ProductDto;
import ru.practicum.enums.order.OrderState;
import ru.practicum.exceptions.NoPaymentFoundException;
import ru.practicum.feign_client.OrderClient;
import ru.practicum.feign_client.StoreClient;
import ru.practicum.feign_client.exception.order.NoOrderFoundException;
import ru.practicum.feign_client.exception.payment.NotEnoughInfoInOrderToCalculateException;
import ru.practicum.feign_client.exception.shopping_store.ProductNotFoundException;
import ru.practicum.mapper.PaymentMapper;
import ru.practicum.model.Payment;
import ru.practicum.model.enums.PaymentState;
import ru.practicum.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final StoreClient storeClient;
    private final OrderClient orderClient;

    @Value("${payment.fee_ratio}")
    private Double feeRatio;

    @Override
    @Transactional
    public PaymentDto makingPaymentForOrder(OrderDto orderDto) {
        if (orderDto.getTotalPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Для оплаты не хватает информации в заказе");
        }

        log.info("Отправляем заказ {} на оплату", orderDto.getOrderId());
        return PaymentMapper.mapToDto(paymentRepository.save(PaymentMapper.mapToPayment(orderDto)), feeRatio);
    }

    @Override
    public Double calculateProductsCost(OrderDto orderDto) {
        try {
            Map<UUID, Long> products = orderDto.getProducts();

            log.info("Запрашиваем стоимость продуктов из shopping-store");
            Map<UUID, Float> productsPrice = products.keySet().stream()
                    .map(storeClient::getProductById)
                    .collect(Collectors.toMap(ProductDto::getProductId, ProductDto::getPrice));

            log.info("Считаем стоимость всех продуктов");
            return products.entrySet().stream()
                    .map(entry -> entry.getValue() * productsPrice.get(entry.getKey()))
                    .mapToDouble(Float::floatValue)
                    .sum();
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ProductNotFoundException("Продукт не найден");
            } else {
                throw e;
            }
        }
    }

    @Override
    public Double calculateTotalCost(OrderDto orderDto) {
        Double productsPrice = orderDto.getProductPrice();
        if (productsPrice == null || orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Не посчитана цена продуктов в заказе или цена доставки");
        }

        log.info("Считаем полную стоимость заказа");
        return productsPrice + productsPrice * feeRatio + orderDto.getDeliveryPrice();
    }

    @Override
    @Transactional
    public void changePaymentStateToSuccess(UUID paymentId) {
        Payment oldPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException("Сведений об оплате не найдено"));

        log.info("Меняес статус оплаты на {}", PaymentState.SUCCESS);
        oldPayment.setState(PaymentState.SUCCESS);

        try {
            log.info("Запрос на смену статуса заказа на {}", OrderState.PAID);
            orderClient.payOrder(oldPayment.getOrderId());
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new NoOrderFoundException(e.getMessage());
            } else {
                throw e;
            }
        }
    }

    @Override
    @Transactional
    public void changePaymentStateToFailed(UUID paymentId) {
        Payment oldPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoPaymentFoundException("Сведений об оплате не найдено"));

        log.info("Меняем статус оплаты на {}", PaymentState.FAILED);
        oldPayment.setState(PaymentState.FAILED);

        try {
            log.info("Запрос на смену статуса заказа на {}", OrderState.PAYMENT_FAILED);
            orderClient.payOrderFailed(oldPayment.getOrderId());
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new NoOrderFoundException(e.getMessage());
            } else {
                throw e;
            }
        }
    }
}
