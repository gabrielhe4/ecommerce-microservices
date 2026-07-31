package io.github.gabrielhe4.product_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductRequest (

    @NotNull
    @NotBlank(message = "SKU is required")
    String sku,

    @NotNull
    @NotBlank(message = "Name is required")
    String name,

    @Size(max = 200, message = "Restricted to 200 characters length")
    String description,

    @NotNull
    @Positive(message = "Only positive numbers for price")
    BigDecimal price,

    @NotNull
    @Positive(message = "Only positive numbers for discount percentage")
    BigDecimal discountPercentage,

    LocalDateTime discountStartsAt,

    LocalDateTime discountEndsAt,

    @NotNull
    @Positive(message = "Only positive numbers for categoryId")
    @NotBlank(message = "Category ID is required")
    Long categoryId
){

}
