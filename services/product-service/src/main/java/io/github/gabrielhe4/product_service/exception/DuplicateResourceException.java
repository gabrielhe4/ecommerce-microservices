package io.github.gabrielhe4.product_service.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String resource, String field) {
        super("%s already exists: %s".formatted(resource, field));
    }

}
