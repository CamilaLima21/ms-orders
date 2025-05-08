package br.com.fiap.msorders.application.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.domain.model.OrderItem;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderItemEntity;

class OrderItemMapperTest {

    private OrderItemMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderItemMapper();
    }

    @Test
    void testToEntityFromDomain() {
        OrderEntity order = new OrderEntity();
        OrderItem domain = new OrderItem(1L, 2L, "SKU123", 4, BigDecimal.TEN);

        OrderItemEntity entity = mapper.toEntity(domain, order);

        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getProductSku(), entity.getProductSku());
        assertEquals(domain.getQuantity(), entity.getQuantity());
        assertEquals(domain.getPrice(), entity.getPrice());
        assertEquals(order, entity.getOrder());
    }

    @Test
    void testToEntityReturnsNullWhenDomainOrOrderIsNull() {
        OrderItem validItem = new OrderItem(1L, 2L, "SKU123", 4, BigDecimal.TEN);
        OrderEntity validOrder = new OrderEntity();

        assertNull(mapper.toEntity((OrderItem) null, validOrder));
        assertNull(mapper.toEntity(validItem, null));
    }

    @Test
    void testToDomainFromEntity() {
        OrderEntity order = new OrderEntity();
        order.setId(2L);

        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(1L);
        entity.setOrder(order);
        entity.setProductSku("SKU456");
        entity.setQuantity(4);
        entity.setPrice(BigDecimal.TEN);

        OrderItem domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(order.getId(), domain.getOrderId());
        assertEquals(entity.getProductSku(), domain.getProductSku());
        assertEquals(entity.getQuantity(), domain.getQuantity());
        assertEquals(entity.getPrice(), domain.getPrice());
    }

    @Test
    void testToDomainReturnsNullWhenEntityIsNull() {
        assertNull(mapper.toDomain((OrderItemEntity) null));
    }

    @Test
    void testToDtoFromDomain() {
        OrderItem domain = new OrderItem(1L, 2L, "SKU789", 4, BigDecimal.TEN);

        OrderItemDto dto = mapper.toDto(domain);

        assertNotNull(dto);
        assertEquals(domain.getId(), dto.id());
        assertEquals(domain.getOrderId(), dto.orderId());
        assertEquals(domain.getProductSku(), dto.productSku());
        assertEquals(domain.getQuantity(), dto.quantity());
        assertEquals(domain.getPrice(), dto.price());
    }

    @Test
    void testToDtoFromEntity() {
        OrderEntity order = new OrderEntity();
        order.setId(2L);

        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(1L);
        entity.setOrder(order);
        entity.setProductSku("SKU321");
        entity.setQuantity(4);
        entity.setPrice(BigDecimal.TEN);

        OrderItemDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.id());
        assertEquals(order.getId(), dto.orderId());
        assertEquals(entity.getProductSku(), dto.productSku());
        assertEquals(entity.getQuantity(), dto.quantity());
        assertEquals(entity.getPrice(), dto.price());
    }

    @Test
    void testToDomainFromDto() {
        OrderItemDto dto = new OrderItemDto(1L, 2L, "SKU001", 4, BigDecimal.TEN);

        OrderItem domain = mapper.toDomain(dto);

        assertNotNull(domain);
        assertEquals(dto.id(), domain.getId());
        assertEquals(dto.orderId(), domain.getOrderId());
        assertEquals(dto.productSku(), domain.getProductSku());
        assertEquals(dto.quantity(), domain.getQuantity());
        assertEquals(dto.price(), domain.getPrice());
    }

    @Test
    void testToDtoAndToDomainReturnNullWhenDtoIsNull() {
        assertNull(mapper.toDto((OrderItem) null));
        assertNull(mapper.toDomain((OrderItemDto) null));
    }

    @Test
    void testToEntityFromDto() {
        OrderItemDto dto = new OrderItemDto(1L, 2L, "SKU777", 4, BigDecimal.TEN);
        OrderEntity order = new OrderEntity();

        OrderItemEntity entity = mapper.toEntity(dto, order);

        assertNotNull(entity);
        assertEquals(dto.id(), entity.getId());
        assertEquals(dto.productSku(), entity.getProductSku());
        assertEquals(dto.quantity(), entity.getQuantity());
        assertEquals(dto.price(), entity.getPrice());
        assertEquals(order, entity.getOrder());
    }

    @Test
    void testUpdateEntityFromDto() {
        OrderItemDto dto = new OrderItemDto(1L, 2L, "SKU987", 4, BigDecimal.TEN);
        OrderItemEntity entity = new OrderItemEntity();
        OrderEntity order = new OrderEntity();

        mapper.updateFromDto(dto, entity, order);

        assertEquals(dto.productSku(), entity.getProductSku());
        assertEquals(dto.quantity(), entity.getQuantity());
        assertEquals(dto.price(), entity.getPrice());
        assertEquals(order, entity.getOrder());
    }

    @Test
    void testUpdateEntityFromDtoWithNulls() {
        assertDoesNotThrow(() -> mapper.updateFromDto(null, null, null));
    }

    @Test
    void testToDtoList() {
        OrderItemEntity entity1 = new OrderItemEntity();
        entity1.setId(1L);
        entity1.setProductSku("SKU100");
        entity1.setQuantity(2);
        entity1.setPrice(BigDecimal.valueOf(50));

        OrderItemEntity entity2 = new OrderItemEntity();
        entity2.setId(2L);
        entity2.setProductSku("SKU200");
        entity2.setQuantity(1);
        entity2.setPrice(BigDecimal.valueOf(100));

        List<OrderItemDto> dtos = mapper.toDtoList(List.of(entity1, entity2));

        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        assertEquals("SKU100", dtos.get(0).productSku());
        assertEquals("SKU200", dtos.get(1).productSku());
    }
}
