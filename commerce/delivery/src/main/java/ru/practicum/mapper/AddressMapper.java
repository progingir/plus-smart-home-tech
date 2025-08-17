package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.warehouse.AddressDto;
import ru.practicum.model.Address;

@UtilityClass
public class AddressMapper {

    public Address mapToAddress(AddressDto dto) {
        return Address.builder()
                .house(dto.getHouse())
                .flat(dto.getFlat())
                .city(dto.getCity())
                .street(dto.getStreet())
                .country(dto.getCountry())
                .build();
    }

    public AddressDto mapToDto(Address address) {
        return AddressDto.builder()
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .country(address.getCountry())
                .flat(address.getFlat())
                .build();
    }
}
