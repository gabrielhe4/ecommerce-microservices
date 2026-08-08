package io.github.gabrielhe4.product_service.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) { 
        super("Category not found with ID: " + id);
    }

}
