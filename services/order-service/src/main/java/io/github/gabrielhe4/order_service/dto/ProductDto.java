package io.github.gabrielhe4.order_service.dto;

import java.math.BigDecimal;

public record ProductDto(
    Long id,
    String name,
    BigDecimal finalPrice
) {

}
