package io.github.gabrielhe4.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.gabrielhe4.inventory_service.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Long> {

}
