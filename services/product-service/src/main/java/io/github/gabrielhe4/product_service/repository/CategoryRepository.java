package io.github.gabrielhe4.product_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.gabrielhe4.product_service.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
    Optional<Category> findByName(String name);

}