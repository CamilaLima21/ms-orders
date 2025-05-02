package br.com.fiap.msorders.domain.model;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class OrderItem {
    private final long id;
    private final long orderId;
    private final long productId;
    private final Integer quantity;
    private final BigDecimal price;

    public OrderItem(long id, long orderId, long productId, Integer quantity, BigDecimal price) {
        if (quantity == null || quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price must be non-negative");

        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItem withOrderId(long orderId) {
        return new OrderItem(this.id, orderId, this.productId, this.quantity, this.price);
    }
}