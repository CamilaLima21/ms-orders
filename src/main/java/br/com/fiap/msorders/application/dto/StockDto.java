package br.com.fiap.msorders.application.dto;

public record StockDto(
        Long id,
        String sku,
        Integer quantity
) {}
