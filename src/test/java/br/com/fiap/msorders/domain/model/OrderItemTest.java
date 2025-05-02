package br.com.fiap.msorders.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void shouldCreateOrderItemSuccessfully() {
        // Setup
        long id = 1L;
        long orderId = 2L;
        long productId = 3L;
        Integer quantity = 5;
        BigDecimal price = BigDecimal.valueOf(100);

        // Action
        OrderItem orderItem = new OrderItem(id, orderId, productId, quantity, price);

        // Assert
        assertNotNull(orderItem);
        assertEquals(id, orderItem.getId());
        assertEquals(orderId, orderItem.getOrderId());
        assertEquals(productId, orderItem.getProductId());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(price, orderItem.getPrice());
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsInvalid() {
        // Setup
        long id = 1L;
        long orderId = 2L;
        long productId = 3L;
        Integer invalidQuantity = -1; // Invalid quantity
        BigDecimal price = BigDecimal.valueOf(100);

        // Action and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new OrderItem(id, orderId, productId, invalidQuantity, price)
        );
        assertEquals("Quantity must be greater than 0", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNegative() {
        // Setup
        long id = 1L;
        long orderId = 2L;
        long productId = 3L;
        Integer quantity = 5;
        BigDecimal invalidPrice = BigDecimal.valueOf(-10); // Invalid price

        // Action and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new OrderItem(id, orderId, productId, quantity, invalidPrice)
        );
        assertEquals("Price must be non-negative", exception.getMessage());
    }

    @Test
    void shouldCreateOrderItemWithOrderIdUpdated() {
        // Setup
        long id = 1L;
        long initialOrderId = 2L;
        long productId = 3L;
        Integer quantity = 5;
        BigDecimal price = BigDecimal.valueOf(100);

        // Create initial order item
        OrderItem orderItem = new OrderItem(id, initialOrderId, productId, quantity, price);

        // Action - Update orderId
        long newOrderId = 4L;
        OrderItem updatedOrderItem = orderItem.withOrderId(newOrderId);

        // Assert
        assertNotNull(updatedOrderItem);
        assertEquals(newOrderId, updatedOrderItem.getOrderId());
        assertEquals(id, updatedOrderItem.getId()); // Ensure other fields remain the same
        assertEquals(productId, updatedOrderItem.getProductId());
        assertEquals(quantity, updatedOrderItem.getQuantity());
        assertEquals(price, updatedOrderItem.getPrice());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsNull() {
        // Setup
        long id = 1L;
        long orderId = 2L;
        long productId = 3L;
        Integer quantity = 5;
        BigDecimal nullPrice = null; // Null price

        // Action and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new OrderItem(id, orderId, productId, quantity, nullPrice)
        );
        assertEquals("Price must be non-negative", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNull() {
        // Setup
        long id = 1L;
        long orderId = 2L;
        long productId = 3L;
        Integer nullQuantity = null; // Null quantity
        BigDecimal price = BigDecimal.valueOf(100);

        // Action and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            new OrderItem(id, orderId, productId, nullQuantity, price)
        );
        assertEquals("Quantity must be greater than 0", exception.getMessage());
    }
}
