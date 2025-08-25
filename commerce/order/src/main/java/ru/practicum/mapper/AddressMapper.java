package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.warehouse.AddressDto;
import ru.practicum.model.Address;

@UtilityClass
public class AddressMapper {

    public Address mapToAddress(AddressDto addressDto) {
        return Address.builder()
                .country(addressDto.getCountry())
                .city(addressDto.getCity())
                .street(addressDto.getStreet())
                .house(addressDto.getHouse())
                .flat(addressDto.getFlat())
                .build();
    }

    public AddressDto mapToDto(Address address) {
        return AddressDto.builder()
                .country(address.getCountry())
                .city(address.getCity())
                .street(address.getStreet())
                .house(address.getHouse())
                .flat(address.getFlat())
                .build();
    }
}
