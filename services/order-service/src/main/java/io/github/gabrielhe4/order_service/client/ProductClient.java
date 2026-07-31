package io.github.gabrielhe4.order_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import io.github.gabrielhe4.order_service.dto.ProductDto;
import io.github.gabrielhe4.order_service.exception.ResourceNotFoundException;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(RestClient.Builder builder,
        @Value("${services.product.url}") String productUrl) {
            this.restClient = builder.baseUrl(productUrl).build();
    }

    public ProductDto getProduct(Long productId) {
        try {
            return restClient.get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .body(ProductDto.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Product", productId);
        }
    }

}
