package io.github.gabrielhe4.product_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

public record ProductRequest (
    @NotBlank(message = "SKU is required") String sku,
    @NotBlank(message = "Name is required") String name,
    String description,
    BigDecimal price,
    @NotBlank(message = "Category ID is required") Long categoryId
){

}
