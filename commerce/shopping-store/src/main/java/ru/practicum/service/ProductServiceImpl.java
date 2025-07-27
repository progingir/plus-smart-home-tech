package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.store.*;
import ru.practicum.enums.ProductCategory;
import ru.practicum.enums.ProductState;
import ru.practicum.exception.ProductNotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.ProductMapper;
import ru.practicum.model.Product;
import ru.practicum.repository.ProductRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public ProductDto addProduct(ProductDto productDto) {
        log.info("Сохраняем новый товар в БД");
        return ProductMapper.mapToDto(productRepository.save(ProductMapper.mapToProduct(productDto)));
    }

    @Override
    public ProductsListResponse getProductsByCategory(ProductCategory category, ru.practicum.dto.store.Pageable pageable) {
        return null;
    }

    public ProductsListResponse getProductsByCategory(ProductCategory category, Pageable pageable) {
        // Получаем сортировку из pageable
        Sort sort = pageable.getSort();
        // Создаём PageRequest с учётом сортировки
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        // Получаем список заказов сортировки для ответа
        List<Sort.Order> orders = sort.isUnsorted()
                ? Collections.emptyList()
                : sort.get().map(order -> new Sort.Order(order.getDirection(), order.getProperty())).toList();

        return ProductsListResponse.builder()
                .content(productRepository
                        .findAllByProductCategory(category, pageRequest)
                        .stream()
                        .map(ProductMapper::mapToDto)
                        .toList())
                .sort(orders.stream()
                        .map(o -> SortProperties.builder()
                                .direction(o.getDirection().toString())
                                .property(o.getProperty())
                                .build())
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        UUID uuid = productDto.getProductId();
        String newProductName = productDto.getProductName();
        String newDescription = productDto.getDescription();
        String newImageSrc = productDto.getImageSrc();
        ProductCategory newProductCategory = productDto.getProductCategory();
        Float newPrice = productDto.getPrice();

        if (uuid == null) {
            throw new ValidationException("id должен быть задан");
        }
        Product oldProduct = findProduct(uuid);

        if (!newProductName.isBlank() && !newProductName.equals(oldProduct.getProductName())) {
            log.info("Обновляем имя");
            oldProduct.setProductName(newProductName);
        }
        if (!newDescription.isBlank() && !newDescription.equals(oldProduct.getDescription())) {
            log.info("Обновляем описание");
            oldProduct.setDescription(newDescription);
        }
        if (!newImageSrc.equals(oldProduct.getImageSrc())) {
            log.info("Обновляем ссылку на фото");
            oldProduct.setImageSrc(newImageSrc);
        }
        if (newProductCategory != null && !newProductCategory.equals(oldProduct.getProductCategory())) {
            log.info("Обновляем категорию");
            oldProduct.setProductCategory(newProductCategory);
        }
        if (newPrice != null && !newPrice.equals(oldProduct.getPrice())) {
            log.info("Обновляем цену");
            oldProduct.setPrice(newPrice);
        }

        return ProductMapper.mapToDto(oldProduct);
    }

    @Override
    @Transactional
    public Boolean deleteProduct(UUID productId) {
        Product oldProduct = findProduct(productId);

        if (oldProduct.getProductState().equals(ProductState.DEACTIVATE)) {
            log.info("Товар уже деактивирован");
            return false;
        }

        log.info("Деактивация товара с id = {}", productId);
        oldProduct.setProductState(ProductState.DEACTIVATE);

        return true;
    }

    @Override
    @Transactional
    public Boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        Product oldProduct = findProduct(request.getProductId());

        if (oldProduct.getQuantityState().equals(request.getQuantityState())) {
            log.info("Такое количество уже задано");
            return false;
        }

        oldProduct.setQuantityState(request.getQuantityState());

        return true;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        Product product = findProduct(productId);

        return ProductMapper.mapToDto(product);
    }

    private Product findProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар не найден"));
    }
}