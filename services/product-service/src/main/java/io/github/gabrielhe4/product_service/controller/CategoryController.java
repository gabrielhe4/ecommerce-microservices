package io.github.gabrielhe4.product_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.gabrielhe4.product_service.dto.CategoryRequest;
import io.github.gabrielhe4.product_service.dto.CategoryResponse;
import io.github.gabrielhe4.product_service.service.CategoryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/category")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        
        var response = categoryService.createCategory(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        
    }
    

}
