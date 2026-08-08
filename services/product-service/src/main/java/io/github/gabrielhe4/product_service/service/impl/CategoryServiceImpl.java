package io.github.gabrielhe4.product_service.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.gabrielhe4.product_service.dto.CategoryRequest;
import io.github.gabrielhe4.product_service.dto.CategoryResponse;
import io.github.gabrielhe4.product_service.exception.CategoryNotFoundException;
import io.github.gabrielhe4.product_service.exception.ConflictException;
import io.github.gabrielhe4.product_service.exception.DuplicateResourceException;
import io.github.gabrielhe4.product_service.model.Category;
import io.github.gabrielhe4.product_service.repository.CategoryRepository;
import io.github.gabrielhe4.product_service.repository.ProductRepository;
import io.github.gabrielhe4.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private static Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating a new category...");

        if (categoryRepository.existsByName(request.name()))
            throw new DuplicateResourceException("Category", request.name());

        Category newCategory = Category.builder()
                                .name(request.name())
                                .description(request.description())
                                .build();

        CategoryResponse response = CategoryResponse.from(
            categoryRepository.save(newCategory)
        );

        log.info("A new category was created with ID: {}", response.id());

        return response;
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("Updating existing category ID: {}", id);

        Category existingCategory = categoryRepository.findById(id).orElseThrow(
            () -> new CategoryNotFoundException(id));

        existingCategory.setName(request.name());
        existingCategory.setDescription(request.description());

        categoryRepository.save(existingCategory);
        log.info("Category with ID: {} was updated successfully.", id);

        return CategoryResponse.from(existingCategory);

    }

    @Override
    public Page<CategoryResponse> findAll(Pageable pageable) {
        log.info("Fetching all categories with pagination...");
        Page<Category> categories = categoryRepository.findAll(pageable);

        return categories.map(CategoryResponse::from);

    }

    @Override
    public void deleteCategory(Long id) {
        log.info("Deleting category with ID: {}", id);

        Category existingCategory = categoryRepository.findById(id).orElseThrow(
            () -> new CategoryNotFoundException(id));
        
        if (productRepository.existsByCategory(existingCategory))
            throw new ConflictException("Cannot delete category with existing products.");

        categoryRepository.delete(existingCategory);
        log.info("Category with ID: {} was deleted successfully.", id);
    }

    @Override
    public CategoryResponse getById(Long id) {
        log.info("Fetching category with ID: {}", id);
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));

        return CategoryResponse.from(category);
    }

}
