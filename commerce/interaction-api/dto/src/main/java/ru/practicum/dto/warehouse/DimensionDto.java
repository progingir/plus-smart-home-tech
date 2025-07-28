package ru.practicum.dto.warehouse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DimensionDto {
    @NotNull
    @Min(value = 1)
    Double width;
    @NotNull
    @Min(value = 1)
    Double height;
    @NotNull
    @Min(value = 1)
    Double depth;
}
