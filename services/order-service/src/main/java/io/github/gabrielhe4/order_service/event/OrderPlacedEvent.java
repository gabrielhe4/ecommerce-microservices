package io.github.gabrielhe4.order_service.event;

import java.util.List;

public record OrderPlacedEvent(
    Long orderId,
    List<Line> lines
) {
    public record Line(Long productId, Integer quantity) { }
}
