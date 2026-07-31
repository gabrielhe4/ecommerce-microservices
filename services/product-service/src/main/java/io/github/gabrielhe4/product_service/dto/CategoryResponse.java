package io.github.gabrielhe4.product_service.dto;

import io.github.gabrielhe4.product_service.model.Category;

public record CategoryResponse (
    Long id,
    String name, 
    String description
) {
    public static CategoryResponse from(Category entity) {
        return new CategoryResponse(
            entity.getId(),
            entity.getName(),
            entity.getDescription()
        );
    }

}
