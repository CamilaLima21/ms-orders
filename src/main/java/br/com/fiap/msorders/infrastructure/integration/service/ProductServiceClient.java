package br.com.fiap.msorders.infrastructure.integration.service;

import br.com.fiap.msorders.domain.exception.ProductNotFoundException;
import br.com.fiap.msorders.infrastructure.integration.client.ProductClient;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceClient {

    private final ProductClient productClient;

    public ProductServiceClient(ProductClient productClient) {
        this.productClient = productClient;
    }

    public void validateSkus(List<String> skus) {
        try {
            productClient.validateSkus(skus);
        } catch (FeignException.NotFound e) {
            throw new ProductNotFoundException(skus);
        }
    }
}
