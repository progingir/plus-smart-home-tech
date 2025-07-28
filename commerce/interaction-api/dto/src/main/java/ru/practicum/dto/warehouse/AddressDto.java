package ru.practicum.dto.warehouse;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class  AddressDto {
    String country;
    String city;
    String street;
    String house;
    String flat;
}
