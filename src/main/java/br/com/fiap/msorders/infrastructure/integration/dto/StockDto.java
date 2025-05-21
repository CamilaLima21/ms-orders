package br.com.fiap.msorders.infrastructure.integration.dto;

public record StockDto(
        Long id,
        String sku,
        Integer quantity
) {}
