package ru.practicum.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.delivery.DeliveryDto;
import ru.practicum.dto.order.OrderDto;
import ru.practicum.dto.warehouse.AddressDto;
import ru.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.practicum.enums.delivery.DeliveryState;
import ru.practicum.enums.order.OrderState;
import ru.practicum.exceptions.NoDeliveryFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.feign_client.OrderClient;
import ru.practicum.feign_client.WarehouseClient;
import ru.practicum.feign_client.exception.warehouse.OrderBookingNotFoundException;
import ru.practicum.mapper.AddressMapper;
import ru.practicum.mapper.DeliveryMapper;
import ru.practicum.model.Address;
import ru.practicum.model.Delivery;
import ru.practicum.repository.AddressRepository;
import ru.practicum.repository.DeliveryRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final AddressRepository addressRepository;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Value("${delivery.base_cost}")
    private Double baseCost;
    @Value("${delivery.warehouse_address_ratio}")
    private Integer warehouseAddressRatio;
    @Value("${delivery.fragile_ratio}")
    private Double fragileRatio;
    @Value("${delivery.weight_ratio}")
    private Double weightRatio;
    @Value("${delivery.volume_ratio}")
    private Double volumeRatio;
    @Value("${delivery.delivery_address_ratio}")
    private Double deliveryAddressRatio;

    private static final String FIRST_ADDRESS = "ADDRESS_1";
    private static final String SECOND_ADDRESS = "ADDRESS_2";

    @Override
    @Transactional
    public DeliveryDto createNewDelivery(DeliveryDto deliveryDto) {
        Address fromAddress = addressRepository.save(AddressMapper.mapToAddress(deliveryDto.getFromAddress()));
        Address toAddress = addressRepository.save(AddressMapper.mapToAddress(deliveryDto.getToAddress()));

        log.info("Создаём новую доставку из склада по адресу {} до заказчика по адресу {}", fromAddress, toAddress);
        return DeliveryMapper.mapToDto(deliveryRepository.save(DeliveryMapper.mapToDelivery(deliveryDto, fromAddress,
                toAddress)));
    }

    @Override
    @Transactional
    public Double calculateDeliveryCost(OrderDto orderDto) {
        UUID deliveryId = orderDto.getDeliveryId();
        if (deliveryId == null) {
            throw new ValidationException("Не задан deliveryId");
        }

        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setDeliveryWeight(orderDto.getDeliveryWeight());
        delivery.setDeliveryVolume(orderDto.getDeliveryVolume());
        delivery.setFragile(orderDto.getFragile());

        log.info("Рассчитываем стоимость доставки для заказа orderId = {}", orderDto.getOrderId());
        double result = baseCost;
        AddressDto warehouseAddress = AddressMapper.mapToDto(delivery.getFromAddress());
        if (warehouseAddress.getStreet().equals(SECOND_ADDRESS) && warehouseAddress.getCity().equals(SECOND_ADDRESS) &&
                warehouseAddress.getCountry().equals(SECOND_ADDRESS)) {
            result = result + result * warehouseAddressRatio;
        } else if (warehouseAddress.getStreet().equals(FIRST_ADDRESS) &&
                warehouseAddress.getCity().equals(FIRST_ADDRESS) &&
                warehouseAddress.getCountry().equals(FIRST_ADDRESS)) {
            result += result;
        }
        if (orderDto.getFragile()) {
            result = result + result * fragileRatio;
        }
        result = result + orderDto.getDeliveryWeight() * weightRatio;
        result = result + orderDto.getDeliveryVolume() * volumeRatio;
        Address deliveryAddress = delivery.getToAddress();
        if (!deliveryAddress.getStreet().equals(warehouseAddress.getStreet()) &&
                !deliveryAddress.getCity().equals(warehouseAddress.getCity()) &&
                !deliveryAddress.getCountry().equals(warehouseAddress.getCountry())) {
            result = result + result * deliveryAddressRatio;
        }
        log.info("Получили стоимость доставки = {}", result);

        return result;
    }

    @Override
    @Transactional
    public void sendProductsToDelivery(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);

        try {
            log.info("Отправляем запрос на склад для передачи товаров в доставку");
            warehouseClient.shipProductsToDelivery(ShippedToDeliveryRequest.builder()
                    .deliveryId(deliveryId)
                    .orderId(delivery.getOrderId())
                    .build());
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new OrderBookingNotFoundException(e.getMessage());
            } else {
                throw e;
            }
        }

        log.info("Меняем статус доставки на {}", DeliveryState.IN_PROGRESS);
        delivery.setState(DeliveryState.IN_PROGRESS);
    }

    @Override
    @Transactional
    public void changeStateToDelivered(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);

        log.info("Меняем статус доставки на {}", DeliveryState.DELIVERED);
        delivery.setState(DeliveryState.DELIVERED);

        log.info("Отправляем запрос на изменение статуса заказа на {}", OrderState.DELIVERED);
        orderClient.sendOrderToDelivery(delivery.getOrderId());
    }

    @Override
    public void changeStateToFailed(UUID deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);

        log.info("Меняем статус доставки на {}", DeliveryState.FAILED);
        delivery.setState(DeliveryState.FAILED);

        log.info("Отправляем запрос на изменение статуса заказа на {}", OrderState.DELIVERY_FAILED);
        orderClient.changeStateToDeliveryFailed(delivery.getOrderId());
    }

    private Delivery getDeliveryById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException("Доставки с id = %s не существует"
                        .formatted(deliveryId)));
    }
}
