package io.github.gabrielhe4.product_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequest (
    @NotNull(message = "Name is required")
    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
    String name,

    @Size(max = 500, message = "Description must not exceed 50 characters")
    String description
){

}
