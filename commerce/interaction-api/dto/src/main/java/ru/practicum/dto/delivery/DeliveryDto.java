package ru.practicum.dto.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.dto.warehouse.AddressDto;
import ru.practicum.enums.delivery.DeliveryState;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class DeliveryDto {
    UUID deliveryId;
    @NotNull
    AddressDto fromAddress;
    @NotNull
    AddressDto toAddress;
    @NotNull
    UUID orderId;
    @NotNull
    DeliveryState state;
}
