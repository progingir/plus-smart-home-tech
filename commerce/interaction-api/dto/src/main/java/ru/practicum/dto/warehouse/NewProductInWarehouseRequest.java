package ru.practicum.dto.warehouse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewProductInWarehouseRequest {
    @NotNull
    UUID productId;
    Boolean fragile;
    @NotNull
    @Valid
    DimensionDto dimension;
    @NotNull
    @Min(value = 1)
    Double weight;
}
