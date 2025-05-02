package br.com.fiap.msorders.infrastructure.persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.fiap.msorders.domain.enums.OrderStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemEntityTest {

    private OrderEntity orderEntity;
    private OrderItemEntity orderItemEntity;

    private long productId;
    private int quantity;
    private BigDecimal price;

    @BeforeEach
    void setUp() {
        // Setup dados para OrderEntity
        orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setClientId(100L);
        orderEntity.setTotal(BigDecimal.valueOf(200.00));
        orderEntity.setStatus(OrderStatus.CREATED);

        // Setup dados para OrderItemEntity
        productId = 101L;
        quantity = 2;
        price = BigDecimal.valueOf(50.00);

        // Criar e associar a OrderItemEntity
        orderItemEntity = new OrderItemEntity();
        orderItemEntity.setProductId(productId);
        orderItemEntity.setQuantity(quantity);
        orderItemEntity.setPrice(price);
        orderEntity.addOrderItem(orderItemEntity); // usa método seguro
    }

    @Test
    void shouldCreateOrderItemEntitySuccessfully() {
        assertNotNull(orderItemEntity);
        assertEquals(orderEntity, orderItemEntity.getOrder());
        assertEquals(productId, orderItemEntity.getProductId());
        assertEquals(quantity, orderItemEntity.getQuantity());
        assertEquals(price, orderItemEntity.getPrice());
    }

    @Test
    void shouldAddOrderItemToOrderSuccessfully() {
        OrderItemEntity newItem = new OrderItemEntity();
        newItem.setProductId(102L);
        newItem.setQuantity(3);
        newItem.setPrice(BigDecimal.valueOf(60.00));

        orderEntity.addOrderItem(newItem); // usa método seguro

        assertEquals(2, orderEntity.getOrderItems().size());
        assertTrue(orderEntity.getOrderItems().contains(newItem));
        assertEquals(orderEntity, newItem.getOrder()); // confirmação do vínculo
    }

    @Test
    void shouldRemoveOrderItemFromOrderSuccessfully() {
        orderEntity.removeOrderItem(orderItemEntity); // usa método seguro

        assertEquals(0, orderEntity.getOrderItems().size());
        assertNull(orderItemEntity.getOrder());
    }

    @Test
    void shouldSetAndGetOrderSuccessfully() {
        OrderEntity newOrderEntity = new OrderEntity();
        newOrderEntity.setId(2L);

        orderItemEntity.setOrder(newOrderEntity);

        assertEquals(newOrderEntity, orderItemEntity.getOrder());
    }

    @Test
    void shouldUpdateProductIdSuccessfully() {
        orderItemEntity.setProductId(103L);
        assertEquals(103L, orderItemEntity.getProductId());
    }

    @Test
    void shouldUpdateQuantitySuccessfully() {
        orderItemEntity.setQuantity(5);
        assertEquals(5, orderItemEntity.getQuantity());
    }

    @Test
    void shouldUpdatePriceSuccessfully() {
        orderItemEntity.setPrice(BigDecimal.valueOf(75.00));
        assertEquals(BigDecimal.valueOf(75.00), orderItemEntity.getPrice());
    }

    @Test
    void shouldAllowNegativePriceWithoutException() {
        // Nenhuma validação na entidade impede valor negativo, então não deve lançar exceção
        OrderItemEntity item = new OrderItemEntity();
        item.setPrice(BigDecimal.valueOf(-10.00));
        assertEquals(BigDecimal.valueOf(-10.00), item.getPrice());
    }

    @Test
    void shouldAllowZeroOrNegativeQuantityWithoutException() {
        OrderItemEntity item1 = new OrderItemEntity();
        item1.setQuantity(0);
        assertEquals(0, item1.getQuantity());

        OrderItemEntity item2 = new OrderItemEntity();
        item2.setQuantity(-1);
        assertEquals(-1, item2.getQuantity());
    }
}
