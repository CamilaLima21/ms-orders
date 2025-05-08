package br.com.fiap.msorders.application.dto;

import java.math.BigDecimal;

public record OrderItemDto(
    long id,
    long orderId,
    String productSku,
    Integer quantity,
    BigDecimal price
) {}