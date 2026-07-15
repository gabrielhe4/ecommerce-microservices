package io.github.gabrielhe4.product_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.gabrielhe4.product_service.dto.CategoryRequest;
import io.github.gabrielhe4.product_service.dto.CategoryResponse;
import io.github.gabrielhe4.product_service.exception.CategoryNotFoundException;
import io.github.gabrielhe4.product_service.model.Category;
import io.github.gabrielhe4.product_service.repository.CategoryRepository;
import io.github.gabrielhe4.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category newCategory = Category.builder()
                                .name(request.name())
                                .description(request.description())
                                .build();

        CategoryResponse response = CategoryResponse.from(
            categoryRepository.save(newCategory)
        );

        return response;
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(
            () -> new CategoryNotFoundException(id));

        existingCategory.setName(request.name());
        existingCategory.setDescription(request.description());

        categoryRepository.save(existingCategory);

        return CategoryResponse.from(existingCategory);

    }

    @Override
    public Page<CategoryResponse> findAll(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);

        return categories.map(CategoryResponse::from);

    }

    @Override
    public void deleteCategory(Long id) {
        Category existingCategory = categoryRepository.findById(id).orElseThrow(
            () -> new CategoryNotFoundException(id));
            
        categoryRepository.delete(existingCategory);
    }

}
