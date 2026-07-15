package io.github.gabrielhe4.product_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.gabrielhe4.product_service.dto.ProductRequest;
import io.github.gabrielhe4.product_service.dto.ProductResponse;
import io.github.gabrielhe4.product_service.exception.CategoryNotFoundException;
import io.github.gabrielhe4.product_service.exception.ProductNotFoundException;
import io.github.gabrielhe4.product_service.model.Category;
import io.github.gabrielhe4.product_service.model.Product;
import io.github.gabrielhe4.product_service.repository.CategoryRepository;
import io.github.gabrielhe4.product_service.repository.ProductRepository;
import io.github.gabrielhe4.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public ProductResponse create(ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        Product newProduct = Product.builder()
                                .name(request.name())
                                .sku(request.sku())
                                .description(request.description())
                                .price(request.price())
                                .category(category)
                                .build();
        
        return ProductResponse.from(
            productRepository.save(newProduct)
        );
    }

    @Override
    public Page<ProductResponse> findAll(String search, Pageable pageable) {
        Page<Product> page;

        if (search != null && !search.isEmpty()) {
            page = productRepository.searchByNameContainingIgnoreCase(search, pageable);
        } else {
            page = productRepository.findAll(pageable);
        }

        return page.map(ProductResponse::from);
    }

    @Override
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
            .map(ProductResponse::from)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public ProductResponse findBySku(String sku) {
        return productRepository.findBySku(sku)
            .map(ProductResponse::from)
            .orElseThrow(() -> new ProductNotFoundException(sku));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id))
            throw new ProductNotFoundException(id);

        productRepository.deleteById(id);
        
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));
        
        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(category);

        return ProductResponse.from(
            productRepository.save(product)
        );
    }

}
