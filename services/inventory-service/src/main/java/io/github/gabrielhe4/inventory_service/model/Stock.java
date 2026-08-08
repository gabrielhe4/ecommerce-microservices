package io.github.gabrielhe4.inventory_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "stock")
@AllArgsConstructor
public class Stock {

    @Getter
    @Id
    private Long productId;

    @Getter
    @Setter
    @Column(nullable = false)
    private Integer quantity;

    protected Stock() {}

}
