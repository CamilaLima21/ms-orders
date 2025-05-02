package br.com.fiap.msorders.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.msorders.domain.enums.OrderStatus;

public record OrderDto(
    long id,  
    long clientId,  
    BigDecimal total,  
    OrderStatus status,  
    LocalDateTime createdAt,  
    LocalDateTime updatedAt,  
    List<OrderItemDto> item  
) {}
