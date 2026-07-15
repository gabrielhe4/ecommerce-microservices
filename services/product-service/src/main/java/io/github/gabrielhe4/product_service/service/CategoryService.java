package io.github.gabrielhe4.product_service.service;

import io.github.gabrielhe4.product_service.dto.CategoryRequest;
import io.github.gabrielhe4.product_service.dto.CategoryResponse;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id,  CategoryRequest request);

}
