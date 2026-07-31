package io.github.gabrielhe4.product_service.service;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import io.github.gabrielhe4.product_service.dto.ProductRequest;
import io.github.gabrielhe4.product_service.dto.ProductResponse;

public interface ProductService {

    ProductResponse create(ProductRequest request);

    Page<ProductResponse> findAll(String search, Long categoryId, Pageable pageable);

    ProductResponse findById(Long id);

    ProductResponse findBySku(String sku);

    void delete(Long id);

    ProductResponse update(Long id, ProductRequest request);

    ProductResponse updateImage(Long id, MultipartFile image) throws IOException;

    void deleteProduct(Long id);

}
