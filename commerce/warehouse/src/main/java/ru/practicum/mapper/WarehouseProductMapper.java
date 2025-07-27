package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.practicum.model.WarehouseProduct;

import java.util.Objects;

@UtilityClass
public class WarehouseProductMapper {

    public WarehouseProduct mapToProduct(NewProductInWarehouseRequest request) {
        return WarehouseProduct.builder()
                .productId(request.getProductId())
                .fragile(checkFragile(request.getFragile()))
                .weight(request.getWeight())
                .width(request.getDimension().getWidth())
                .depth(request.getDimension().getDepth())
                .height(request.getDimension().getHeight())
                .quantity(0L)
                .build();
    }

    private boolean checkFragile(Boolean fragile) {
        return Objects.requireNonNullElse(fragile, false);
    }
}
