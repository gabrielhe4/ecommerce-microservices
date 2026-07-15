package io.github.gabrielhe4.product_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import io.github.gabrielhe4.product_service.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> searchByNameContainingIgnoreCase(String search, Pageable pageable);

    Optional<Product> findBySku(String sku);

}
