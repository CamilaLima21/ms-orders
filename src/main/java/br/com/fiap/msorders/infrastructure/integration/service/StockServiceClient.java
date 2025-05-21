package br.com.fiap.msorders.infrastructure.integration.service;

import org.springframework.stereotype.Service;

import br.com.fiap.msorders.infrastructure.integration.dto.StockDto;
import br.com.fiap.msorders.infrastructure.integration.client.StockClient;
import br.com.fiap.msorders.infrastructure.web.exceptions.StockNotFoundException;
import feign.FeignException;

@Service
public class StockServiceClient {

    private final StockClient stockClient;

    public StockServiceClient(StockClient stockClient) {
        this.stockClient = stockClient;
    }

    public StockDto searchStock(String sku) {
        try {
            return stockClient.searchStock(sku).getBody();
        } catch (FeignException.NotFound e) {
            throw new StockNotFoundException(sku);
        }
    }
    
    public void increaseStock(String sku, int quantity) {
        try {
            stockClient.increaseStock(sku, quantity);
        } catch (FeignException e) {
            throw new RuntimeException("Failed to increase stock for SKU: " + sku, e);
        }
    }
    
    public void decreaseStock(String sku, int quantity) {
        try {
            stockClient.decreaseStock(sku, quantity);
        } catch (FeignException e) {
            throw new RuntimeException("Failed to decrease stock for SKU: " + sku, e);
        }
    }
}