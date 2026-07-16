package io.github.gabrielhe4.product_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.gabrielhe4.product_service.dto.ProductRequest;
import io.github.gabrielhe4.product_service.dto.ProductResponse;
import io.github.gabrielhe4.product_service.service.ProductService;
import jakarta.validation.Valid;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/product")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {

        var response = productService.create(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponse>> findAllProducts (
        @RequestParam(required = false) String search,
        @RequestParam(required = false) Long categoryId,
        @PageableDefault(size = 10, page = 0, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        var response = productService.findAll(search, categoryId, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/sku/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@RequestParam String sku) {
        
        var response = productService.findBySku(sku);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        
        var response = productService.findById(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
        @PathVariable Long id,
        @RequestBody ProductRequest request
    ) {
        var response = productService.update(id, request);
        
        return ResponseEntity.ok(response);
        
    }

    @PutMapping("products/{id}/image")
    public ResponseEntity<ProductResponse> updateProductImage(
        @PathVariable Long id, 
        @RequestParam("image") MultipartFile image
    ) throws IOException {
        
        var response = productService.updateImage(id, image);
        
        return ResponseEntity.ok(response);
    }
    
    
    


    


}
