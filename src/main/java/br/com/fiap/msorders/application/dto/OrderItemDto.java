package br.com.fiap.msorders.application.dto;

import java.math.BigDecimal;

public record OrderItemDto(
    long id,
    long orderId,
    long productId,
    Integer quantity,
    BigDecimal price
) {}