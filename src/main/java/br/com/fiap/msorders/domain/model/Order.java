package br.com.fiap.msorders.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.msorders.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Order {
    private long id;
    private long clientId;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItem> items;
}
