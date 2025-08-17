package ru.practicum.dto.store;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.enums.store.QuantityState;

import java.util.UUID;

@Getter
@Builder
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;
    @NotNull
    private QuantityState quantityState;
}
