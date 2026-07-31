package io.github.gabrielhe4.product_service.dto;

import java.math.BigDecimal;
import java.time.Instant;

import io.github.gabrielhe4.product_service.model.Product;

public record ProductResponse(
    Long id,
    String sku,
    String name,
    String description,
    BigDecimal price,
    Instant createdAt,
    Instant updatedAt,
    String category,
    String image,
    boolean discountActive,
    BigDecimal discountPercentage
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getSku(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCreatedAt(),
            product.getUpdatedAt(),
            product.getCategory().getName(),
            product.getImageUrl(),
            product.isDiscountActive(),
            product.getDiscountPercentage()
        );
    }

}
