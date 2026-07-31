package io.github.gabrielhe4.order_service.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Long id) {
        super("%s not found: %d".formatted(resource, id));
    }

}
