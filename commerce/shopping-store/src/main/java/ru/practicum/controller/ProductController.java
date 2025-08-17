package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.store.Pageable;
import ru.practicum.dto.store.ProductDto;
import ru.practicum.dto.store.ProductsListResponse;
import ru.practicum.dto.store.SetProductQuantityStateRequest;
import ru.practicum.enums.ProductCategory;
import ru.practicum.service.ProductService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PutMapping
    public ProductDto addProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Получили запрос на добавление товара c productName = {}", productDto.getProductName());
        return productService.addProduct(productDto);
    }

    @GetMapping
    public ProductsListResponse getProductsByCategory(@RequestParam ProductCategory category, @Valid Pageable pageable) {
        log.info("Получили категорию = {} и pageable = {}", category, pageable);
        return productService.getProductsByCategory(category, pageable);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        return productService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    public Boolean deleteProduct(@RequestBody UUID productId) {
        log.info("Запрос на деактивацию товара с id = {}", productId);
        return productService.deleteProduct(productId);
    }

    @PostMapping("/quantityState")
    public Boolean setQuantityState(@Valid SetProductQuantityStateRequest request) {
        return productService.setProductQuantityState(request);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        log.info("Запрос на получение информации о товаре с id = {}", productId);
        return productService.getProductById(productId);
    }
}
