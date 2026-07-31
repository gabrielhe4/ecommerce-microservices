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



@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

     @GetMapping("/categories")
     public ResponseEntity<Page<CategoryResponse>> findAll(
        @PageableDefault(page = 0, size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
     ) {

        var response = categoryService.findAll(pageable);

        return ResponseEntity.ok(response);

     }


}
