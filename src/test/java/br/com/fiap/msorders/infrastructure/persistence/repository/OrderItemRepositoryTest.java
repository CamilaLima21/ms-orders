package br.com.fiap.msorders.infrastructure.persistence.repository;

import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderItemEntity;
import br.com.fiap.msorders.domain.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    private OrderEntity buildOrderEntity() {
        OrderEntity order = new OrderEntity();
        order.setClientId(1L);
        order.setTotal(BigDecimal.valueOf(200.00));
        order.setStatus(OrderStatus.CREATED);
        return orderRepository.save(order); // Save and get order with ID
    }

    private OrderItemEntity buildOrderItemEntity(OrderEntity order) {
        OrderItemEntity item = new OrderItemEntity();
        item.setOrder(order);
        item.setProductId(100L);
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(50.00));
        return item;
    }

    @Test
    @DisplayName("Deve salvar um OrderItem com sucesso")
    void shouldSaveOrderItemSuccessfully() {
        OrderEntity order = buildOrderEntity();
        OrderItemEntity orderItem = buildOrderItemEntity(order);

        OrderItemEntity savedItem = orderItemRepository.save(orderItem);

        assertNotNull(savedItem.getId());
        assertEquals(order.getId(), savedItem.getOrder().getId());
        assertEquals(100L, savedItem.getProductId());
        assertEquals(2, savedItem.getQuantity());
        assertEquals(BigDecimal.valueOf(50.00), savedItem.getPrice());
    }

    @Test
    @DisplayName("Deve buscar um OrderItem por ID com sucesso")
    void shouldFindOrderItemByIdSuccessfully() {
        OrderEntity order = buildOrderEntity();
        OrderItemEntity orderItem = buildOrderItemEntity(order);
        OrderItemEntity savedItem = orderItemRepository.save(orderItem);

        Optional<OrderItemEntity> foundItem = orderItemRepository.findById(savedItem.getId());

        assertTrue(foundItem.isPresent());
        assertEquals(savedItem.getId(), foundItem.get().getId());
    }

    @Test
    @DisplayName("Deve retornar todos os OrderItems")
    void shouldReturnAllOrderItems() {
        OrderEntity order = buildOrderEntity();
        orderItemRepository.save(buildOrderItemEntity(order));
        orderItemRepository.save(buildOrderItemEntity(order));

        List<OrderItemEntity> items = orderItemRepository.findAll();

        assertEquals(2, items.size());
    }

    @Test
    @DisplayName("Deve deletar um OrderItem por ID com sucesso")
    void shouldDeleteOrderItemById() {
        OrderEntity order = buildOrderEntity();
        OrderItemEntity orderItem = buildOrderItemEntity(order);
        OrderItemEntity savedItem = orderItemRepository.save(orderItem);
        Long id = savedItem.getId();

        orderItemRepository.deleteById(id);

        Optional<OrderItemEntity> foundItem = orderItemRepository.findById(id);
        assertFalse(foundItem.isPresent());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver OrderItems")
    void shouldReturnEmptyListWhenNoOrderItems() {
        List<OrderItemEntity> items = orderItemRepository.findAll();
        assertTrue(items.isEmpty());
    }
}
