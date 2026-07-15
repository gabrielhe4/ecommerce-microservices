package io.github.gabrielhe4.product_service.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) { 
        super("Product not found with ID: " + id);
    }

    public ProductNotFoundException(String sku) {
        super("Product not found with sku: " + sku);
    }
}
