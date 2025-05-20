package br.com.fiap.msorders.infrastructure.web.exceptions;

public class StockNotFoundException extends RuntimeException {

    public StockNotFoundException(String sku) {
        super("Stock not found for product SKU: " + sku);
    }
}