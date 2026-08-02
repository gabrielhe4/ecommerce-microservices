package io.github.gabrielhe4.product_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.github.gabrielhe4.product_service.dto.CategoryResponse;
import io.github.gabrielhe4.product_service.service.CategoryService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

     @GetMapping
     public ResponseEntity<Page<CategoryResponse>> findAll(
        @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
     ) {

        var response = categoryService.findAll(pageable);

        return ResponseEntity.ok(response);

     }

     @GetMapping("/{id}")
     public ResponseEntity<CategoryResponse> getById(@PathVariable Long id) {
        var response = categoryService.getById(id);
        return ResponseEntity.ok(response);
     }
     


}
