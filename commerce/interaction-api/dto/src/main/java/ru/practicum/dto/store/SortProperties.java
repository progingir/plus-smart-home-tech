package ru.practicum.dto.store;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SortProperties {
    private String  direction;
    private String  property;
}
