package br.com.fiap.msorders.infrastructure.persistence.repository;

import br.com.fiap.msorders.domain.enums.OrderStatus;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderItemEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private OrderEntity createOrderEntity() {
        OrderEntity order = new OrderEntity();
        order.setClientId(1L);
        order.setTotal(BigDecimal.valueOf(200.00));
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // Criar e associar OrderItem
        OrderItemEntity item = new OrderItemEntity();
        item.setProductSku("SKU100");
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(100.00));

        order.addOrderItem(item); // garante relacionamento bidirecional
        return order;
    }

    @Test
    @DisplayName("Deve salvar uma ordem com itens corretamente")
    void shouldSaveOrderWithItems() {
        OrderEntity order = createOrderEntity();
        OrderEntity saved = orderRepository.save(order);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getClientId()).isEqualTo(1L);
        assertThat(saved.getOrderItems()).hasSize(1);

        OrderItemEntity savedItem = saved.getOrderItems().get(0);
        assertThat(savedItem.getProductSku()).isEqualTo("SKU100");  // Corrigido para 'productSku'
        assertThat(savedItem.getQuantity()).isEqualTo(2);
        assertThat(savedItem.getPrice()).isEqualByComparingTo("100.00");
        assertThat(savedItem.getOrder()).isEqualTo(saved); // Confirma o vínculo bidirecional
    }

    @Test
    @DisplayName("Deve buscar ordem por ID")
    void shouldFindOrderById() {
        OrderEntity saved = orderRepository.save(createOrderEntity());

        Optional<OrderEntity> result = orderRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Deve retornar todas as ordens existentes")
    void shouldReturnAllOrders() {
        orderRepository.save(createOrderEntity());
        orderRepository.save(createOrderEntity());

        List<OrderEntity> orders = orderRepository.findAll();
        assertThat(orders).hasSize(2);
    }

    @Test
    @DisplayName("Deve deletar uma ordem por ID")
    void shouldDeleteOrderById() {
        OrderEntity saved = orderRepository.save(createOrderEntity());
        Long id = saved.getId();

        orderRepository.deleteById(id);

        Optional<OrderEntity> result = orderRepository.findById(id);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver ordens salvas")
    void shouldReturnEmptyListWhenNoOrdersExist() {
        List<OrderEntity> orders = orderRepository.findAll();
        assertThat(orders).isEmpty();
    }
}
