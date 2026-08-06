package io.github.gabrielhe4.inventory_service.event;

import java.util.List;

// the event - identical copy in BOTH order and inventory service
public record OrderPlacedEvent(
    Long orderId,
    List<Line> lines
) {
    public record Line(Long productId, Integer quantity) { }
}
