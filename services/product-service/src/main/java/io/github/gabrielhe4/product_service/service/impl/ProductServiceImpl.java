package io.github.gabrielhe4.product_service.service.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.github.gabrielhe4.product_service.dto.ProductRequest;
import io.github.gabrielhe4.product_service.dto.ProductResponse;
import io.github.gabrielhe4.product_service.exception.CategoryNotFoundException;
import io.github.gabrielhe4.product_service.exception.ProductNotFoundException;
import io.github.gabrielhe4.product_service.model.Category;
import io.github.gabrielhe4.product_service.model.Product;
import io.github.gabrielhe4.product_service.repository.CategoryRepository;
import io.github.gabrielhe4.product_service.repository.ProductRepository;
import io.github.gabrielhe4.product_service.service.FileService;
import io.github.gabrielhe4.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final FileService fileService;

    private static Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public ProductResponse create(ProductRequest request) {
        log.info("Creating new product...");

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        Product newProduct = Product.builder()
                                .name(request.name())
                                .sku(request.sku())
                                .description(request.description())
                                .price(request.price())
                                .imageUrl("default-image.jpg")
                                .category(category)
                                .build();

        newProduct = productRepository.save(newProduct);
        log.info("New product was created with ID: {}", newProduct.getId());
        return ProductResponse.from(newProduct);
    }

    @Override
    public Page<ProductResponse> findAll(String search,  Long categoryId, Pageable pageable) {
        log.info("Fetching all products with pagination...");
        Page<Product> page;

        if (search != null)
            page = productRepository.searchByNameContainingIgnoreCase(search, pageable);

        if (categoryId != null) {

            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

            page = productRepository.findByCategory(category, pageable);

        } else {
            page = productRepository.findAll(pageable);
        }

        return page.map(ProductResponse::from);
    }

    @Override
    public ProductResponse findById(Long id) {
        log.info("Fetching product with ID: {}", id);
        return productRepository.findById(id)
            .map(ProductResponse::from)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public ProductResponse findBySku(String sku) {
        log.info("Fetching product with SKU: {}", sku);
        return productRepository.findBySku(sku)
            .map(ProductResponse::from)
            .orElseThrow(() -> new ProductNotFoundException(sku));
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting product with ID: {}", id);
        if (!productRepository.existsById(id))
            throw new ProductNotFoundException(id);

        productRepository.deleteById(id);
        log.info("Product deleted successfully");

    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        log.info("Updating product with ID: {}", id);
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setDiscountStartsAt(request.discountStartsAt());
        product.setDiscountEndsAt(request.discountEndsAt());
        product.setDiscountPercentage(request.discountPercentage());
        product.setCategory(category);

        product = productRepository.save(product);
        log.info("Product was updated successfully");
        return ProductResponse.from(product);
    }

    @Override
    public ProductResponse updateImage(Long id, MultipartFile image) throws IOException {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        // TODO fix url path
        String fileName = fileService.uploadImage(null, image);

        Product updatedProduct = productRepository.save(product);
        updatedProduct.setImageUrl(fileName);

        return ProductResponse.from(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));

        // TODO remove image from path

        productRepository.delete(product);

    }

}
