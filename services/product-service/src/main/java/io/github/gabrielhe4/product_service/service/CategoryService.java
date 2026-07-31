package io.github.gabrielhe4.product_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.gabrielhe4.product_service.dto.CategoryRequest;
import io.github.gabrielhe4.product_service.dto.CategoryResponse;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id,  CategoryRequest request);

    Page<CategoryResponse> findAll(Pageable pageable);

    void deleteCategory(Long id);

    CategoryResponse getById(Long id);

}
