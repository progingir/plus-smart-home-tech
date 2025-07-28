package ru.practicum.dto.store;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductsListResponse {
    private List<SortProperties> sort;
    private List<ProductDto> content;
}

