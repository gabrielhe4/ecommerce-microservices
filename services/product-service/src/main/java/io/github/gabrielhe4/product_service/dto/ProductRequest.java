package io.github.gabrielhe4.product_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRequest (

    @NotNull
    @NotBlank(message = "SKU is required")
    @Size(max = 15, message = "Sku must not exceed 15 characters")
    String sku,

    @NotNull
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    String name,

    @Size(max = 200, message = "Restricted to 200 characters length")
    String description,

    @NotNull
    @Positive(message = "Only positive numbers for price")
    BigDecimal price,

    @Positive(message = "Only positive numbers for discount percentage")
    @Max(100)
    BigDecimal discountPercentage,

    LocalDateTime discountStartsAt,

    LocalDateTime discountEndsAt,

    @NotNull
    @Positive(message = "Only positive numbers for categoryId")
    Long categoryId
){

    public ProductRequest {
    }
        
}
