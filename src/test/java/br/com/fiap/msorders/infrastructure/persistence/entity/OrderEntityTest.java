package br.com.fiap.msorders.infrastructure.persistence.entity;

import br.com.fiap.msorders.domain.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderEntityTest {

    private OrderEntity orderEntity;
    private OrderItemEntity orderItem1;
    private OrderItemEntity orderItem2;

    private long orderId;
    private long clientId;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @BeforeEach
    void setUp() {
        // Dados para a OrderEntity
        orderId = 1L;
        clientId = 101L;
        total = BigDecimal.valueOf(500.00);
        status = OrderStatus.CREATED;
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // Criar a OrderEntity
        orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        orderEntity.setClientId(clientId);
        orderEntity.setTotal(total);
        orderEntity.setStatus(status);
        orderEntity.setCreatedAt(createdAt);
        orderEntity.setUpdatedAt(updatedAt);

        // Criar os OrderItemEntity associados a essa OrderEntity
        orderItem1 = new OrderItemEntity();
        orderItem1.setOrder(orderEntity);
        orderItem1.setProductId(10L);
        orderItem1.setQuantity(2);
        orderItem1.setPrice(BigDecimal.valueOf(50));

        orderItem2 = new OrderItemEntity();
        orderItem2.setOrder(orderEntity);
        orderItem2.setProductId(11L);
        orderItem2.setQuantity(3);
        orderItem2.setPrice(BigDecimal.valueOf(30));

        // Adicionar os OrderItems à OrderEntity
        orderEntity.setOrderItems(new ArrayList<>());
        orderEntity.getOrderItems().add(orderItem1);
        orderEntity.getOrderItems().add(orderItem2);
    }

    @Test
    void shouldCreateOrderEntitySuccessfully() {
        // Assert initial values
        assertNotNull(orderEntity);
        assertEquals(orderId, orderEntity.getId());
        assertEquals(clientId, orderEntity.getClientId());
        assertEquals(total, orderEntity.getTotal());
        assertEquals(status, orderEntity.getStatus());
        assertEquals(createdAt, orderEntity.getCreatedAt());
        assertEquals(updatedAt, orderEntity.getUpdatedAt());
        assertEquals(2, orderEntity.getOrderItems().size());
    }

    @Test
    void shouldAddOrderItemToOrder() {
        // Setup new order item
        OrderItemEntity newOrderItem = new OrderItemEntity();
        newOrderItem.setOrder(orderEntity);
        newOrderItem.setProductId(12L);
        newOrderItem.setQuantity(1);
        newOrderItem.setPrice(BigDecimal.valueOf(100));

        // Action: Add the new order item
        orderEntity.getOrderItems().add(newOrderItem);

        // Assert the size of the list and the added item
        assertEquals(3, orderEntity.getOrderItems().size());
        assertTrue(orderEntity.getOrderItems().contains(newOrderItem));
    }

    @Test
    void shouldRemoveOrderItemFromOrder() {
        // Setup initial order item to be removed
        OrderItemEntity orderItemToRemove = orderEntity.getOrderItems().get(0);

        // Action: Remove the order item
        orderEntity.getOrderItems().remove(orderItemToRemove);

        // Assert
        assertEquals(1, orderEntity.getOrderItems().size());
        assertFalse(orderEntity.getOrderItems().contains(orderItemToRemove));
    }

    @Test
    void shouldSetAndGetOrderItemList() {
        // Corrigido: Usar o construtor padrão e adicionar os itens corretamente
        List<OrderItemEntity> newOrderItems = new ArrayList<>();
        OrderItemEntity newOrderItem = new OrderItemEntity();
        newOrderItem.setOrder(orderEntity);
        newOrderItem.setProductId(13L);
        newOrderItem.setQuantity(4);
        newOrderItem.setPrice(BigDecimal.valueOf(40));
        newOrderItems.add(newOrderItem);

        // Action: Set new order item list
        orderEntity.setOrderItems(newOrderItems);

        // Assert
        assertEquals(1, orderEntity.getOrderItems().size());
        assertEquals(13L, orderEntity.getOrderItems().get(0).getProductId());
    }

    @Test
    void shouldUpdateOrderEntityFields() {
        // Action: Update fields
        orderEntity.setTotal(BigDecimal.valueOf(600));
        orderEntity.setStatus(OrderStatus.PAID);
        LocalDateTime newUpdatedAt = LocalDateTime.now().plusMinutes(10);
        orderEntity.setUpdatedAt(newUpdatedAt);

        // Assert
        assertEquals(BigDecimal.valueOf(600), orderEntity.getTotal());
        assertEquals(OrderStatus.PAID, orderEntity.getStatus());
        assertEquals(newUpdatedAt, orderEntity.getUpdatedAt());
    }

    
    @Test
    void shouldCreateOrderEntityWithEmptyOrderItems() {
        // Setup an OrderEntity with no order items
        OrderEntity emptyOrderEntity = new OrderEntity();
        emptyOrderEntity.setId(orderId);
        emptyOrderEntity.setClientId(clientId);
        emptyOrderEntity.setTotal(total);
        emptyOrderEntity.setStatus(status);
        emptyOrderEntity.setCreatedAt(createdAt);
        emptyOrderEntity.setUpdatedAt(updatedAt);
        emptyOrderEntity.setOrderItems(new ArrayList<>());  // Usar ArrayList aqui

        // Assert
        assertNotNull(emptyOrderEntity);
        assertEquals(0, emptyOrderEntity.getOrderItems().size());
    }
}
