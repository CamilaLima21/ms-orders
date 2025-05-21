package br.com.fiap.msorders.domain.model;

import br.com.fiap.msorders.domain.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderSuccessfully() {
        // Setup
        long orderId = 1L;
        long clientId = 5L;
        BigDecimal total = BigDecimal.valueOf(100);
        OrderStatus status = OrderStatus.CREATED;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        
        // Criando um OrderItem com parâmetros válidos
        OrderItem item = new OrderItem(1L, orderId, "SKU123", 2, BigDecimal.TEN);
        List<OrderItem> items = List.of(item);

        // Action
        Order order = new Order(orderId, clientId, total, status, createdAt, updatedAt, items);

        // Assert
        assertNotNull(order);
        assertEquals(orderId, order.getId());
        assertEquals(clientId, order.getClientId());
        assertEquals(total, order.getTotal());
        assertEquals(status, order.getStatus());
        assertTrue(order.getCreatedAt().isAfter(createdAt.minusSeconds(1)), "Created at should be after the provided time.");
        assertTrue(order.getUpdatedAt().isAfter(updatedAt.minusSeconds(1)), "Updated at should be after the provided time.");
        assertIterableEquals(items, order.getItems(), "Order items should match");
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        // Setup
        long orderId = 1L;
        long clientId = 5L;
        BigDecimal total = BigDecimal.valueOf(100);
        OrderStatus status = OrderStatus.CLOSED_SUCCESS;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        
        // Criando um OrderItem com parâmetros válidos
        OrderItem item = new OrderItem(2L, orderId, "SKU456", 3, BigDecimal.valueOf(15));
        List<OrderItem> items = List.of(item);
        
        // Action
        Order order = new Order(orderId, clientId, total, status, createdAt, updatedAt, items);

        // Test setters and getters
        order.setId(2L);
        order.setClientId(6L);
        order.setTotal(BigDecimal.valueOf(200));
        order.setStatus(OrderStatus.CANCELLED);
        order.setCreatedAt(LocalDateTime.now().plusDays(1));
        order.setUpdatedAt(LocalDateTime.now().plusDays(1));
        order.setItems(List.of(new OrderItem(3L, orderId, "SKU789", 4, BigDecimal.valueOf(20))));

        // Assert
        assertEquals(2L, order.getId());
        assertEquals(6L, order.getClientId());
        assertEquals(BigDecimal.valueOf(200), order.getTotal());
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertNotEquals(createdAt, order.getCreatedAt()); // Ensure the set time is updated
        assertNotEquals(updatedAt, order.getUpdatedAt()); // Ensure the set time is updated
        assertEquals(1, order.getItems().size());
    }

    @Test
    void shouldHandleNullValuesInConstructor() {
        // Setup with null values
        long orderId = 1L;
        long clientId = 5L;
        BigDecimal total = null;
        OrderStatus status = OrderStatus.CREATED;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;
        List<OrderItem> items = null;

        // Action
        Order order = new Order(orderId, clientId, total, status, createdAt, updatedAt, items);

        // Assert
        assertNotNull(order);
        assertEquals(orderId, order.getId());
        assertEquals(clientId, order.getClientId());
        assertNull(order.getTotal()); // Ensure null value is correctly assigned
        assertEquals(status, order.getStatus());
        assertNull(order.getCreatedAt()); // Ensure null value is correctly assigned
        assertNull(order.getUpdatedAt()); // Ensure null value is correctly assigned
        assertNull(order.getItems()); // Ensure null value is correctly assigned
    }

    @Test
    void shouldThrowExceptionForInvalidQuantityAndPrice() {
        // Test for invalid quantity (less than or equal to 0)
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(1L, 1L, "SKU123", 0, BigDecimal.TEN));
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(1L, 1L, "SKU123", -1, BigDecimal.TEN));

        // Test for invalid price (negative price)
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(1L, 1L, "SKU123", 2, BigDecimal.valueOf(-1)));
    }
}
