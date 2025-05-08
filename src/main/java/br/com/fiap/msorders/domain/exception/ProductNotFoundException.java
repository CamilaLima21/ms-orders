package br.com.fiap.msorders.domain.exception;

import java.util.List;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(List<String> skus) {
        super("One or more products not found for SKUs: " + skus);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
