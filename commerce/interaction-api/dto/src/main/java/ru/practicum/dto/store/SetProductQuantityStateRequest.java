package ru.practicum.dto.store;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.enums.QuantityState;

import java.util.UUID;

@Getter
@Builder
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;
    @NotNull
    private QuantityState quantityState;
}
