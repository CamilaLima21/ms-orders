package br.com.fiap.msorders.infrastructure.persistence.repository;

import br.com.fiap.msorders.domain.enums.OrderStatus;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderItemEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private OrderEntity buildOrderEntity() {
        OrderEntity order = new OrderEntity();
        order.setClientId(1L);
        order.setTotal(BigDecimal.valueOf(200.00));
        order.setStatus(OrderStatus.CREATED);

        OrderItemEntity item = new OrderItemEntity();
        item.setProductId(100L);
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(100.00));

        order.addOrderItem(item); // associação bidirecional
        return order;
    }

    @Test
    @DisplayName("Deve salvar uma ordem com sucesso")
    void shouldSaveOrderSuccessfully() {
        OrderEntity order = buildOrderEntity();

        OrderEntity saved = orderRepository.save(order);

        assertNotNull(saved.getId());
        assertEquals(1L, saved.getClientId());
        assertEquals(1, saved.getOrderItems().size());
    }

    @Test
    @DisplayName("Deve buscar ordem por ID com sucesso")
    void shouldFindOrderByIdSuccessfully() {
        OrderEntity order = buildOrderEntity();
        OrderEntity saved = orderRepository.save(order);

        Optional<OrderEntity> result = orderRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Deve retornar todas as ordens")
    void shouldReturnAllOrders() {
        orderRepository.save(buildOrderEntity());
        orderRepository.save(buildOrderEntity());

        List<OrderEntity> orders = orderRepository.findAll();

        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("Deve deletar ordem por ID com sucesso")
    void shouldDeleteOrderById() {
        OrderEntity order = orderRepository.save(buildOrderEntity());
        Long id = order.getId();

        orderRepository.deleteById(id);

        Optional<OrderEntity> result = orderRepository.findById(id);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver ordens")
    void shouldReturnEmptyListWhenNoOrders() {
        List<OrderEntity> orders = orderRepository.findAll();
        assertTrue(orders.isEmpty());
    }
}
