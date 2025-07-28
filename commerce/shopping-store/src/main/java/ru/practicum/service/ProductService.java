package ru.practicum.service;

import ru.practicum.dto.store.Pageable;
import ru.practicum.dto.store.ProductDto;
import ru.practicum.dto.store.ProductsListResponse;
import ru.practicum.dto.store.SetProductQuantityStateRequest;
import ru.practicum.enums.ProductCategory;

import java.util.UUID;

public interface ProductService {
    ProductDto addProduct(ProductDto productDto);

    ProductsListResponse getProductsByCategory(ProductCategory category, Pageable pageable);

    ProductDto updateProduct(ProductDto productDto);

    Boolean deleteProduct(UUID productId);

    Boolean setProductQuantityState(SetProductQuantityStateRequest setProductQuantityState);

    ProductDto getProductById(UUID productId);
}
