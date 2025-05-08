package br.com.fiap.msorders.domain.model;

import java.math.BigDecimal;

import lombok.Getter;

@Getter
public class OrderItem {
    private final long id;
    private final long orderId;
    private final String productSku;
    private final Integer quantity;
    private final BigDecimal price;

    public OrderItem(long id, long orderId, String productSku, Integer quantity, BigDecimal price) {
        if (quantity == null || quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Price must be non-negative");

        this.id = id;
        this.orderId = orderId;
        this.productSku = productSku;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItem withOrderId(long orderId) {
        return new OrderItem(this.id, orderId, this.productSku, this.quantity, this.price);
    }
}