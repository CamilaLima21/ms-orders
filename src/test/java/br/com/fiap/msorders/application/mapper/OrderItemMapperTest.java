package br.com.fiap.msorders.application.mapper;

import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.domain.model.OrderItem;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderItemEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemMapperTest {

    private OrderItemMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderItemMapper();
    }

    @Test
    void shouldMapDomainToEntity() {
        OrderEntity order = new OrderEntity();
        OrderItem domain = new OrderItem(1L, 2L, 3L, 4, BigDecimal.TEN);

        OrderItemEntity entity = mapper.toEntity(domain, order);

        assertNotNull(entity);
        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getProductId(), entity.getProductId());
        assertEquals(domain.getQuantity(), entity.getQuantity());
        assertEquals(domain.getPrice(), entity.getPrice());
        assertEquals(order, entity.getOrder());
    }

    @Test
    void shouldReturnNullWhenDomainOrOrderIsNull() {
        OrderItem validItem = new OrderItem(1L, 2L, 3L, 4, BigDecimal.TEN);
        OrderEntity validOrder = new OrderEntity();

        // Especifica explicitamente os tipos para evitar ambiguidade
        assertNull(mapper.toEntity((OrderItem) null, validOrder));
        assertNull(mapper.toEntity(validItem, (OrderEntity) null));
    }


    @Test
    void shouldMapEntityToDomain() {
        OrderEntity order = new OrderEntity();
        order.setId(2L);

        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(1L);
        entity.setOrder(order);
        entity.setProductId(3L);
        entity.setQuantity(4);
        entity.setPrice(BigDecimal.TEN);

        OrderItem domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals(entity.getId(), domain.getId());
        assertEquals(order.getId(), domain.getOrderId());
        assertEquals(entity.getProductId(), domain.getProductId());
        assertEquals(entity.getQuantity(), domain.getQuantity());
        assertEquals(entity.getPrice(), domain.getPrice());
    }

    @Test
    void shouldReturnNullWhenEntityIsNull() {
        assertNull(mapper.toDomain((OrderItemEntity) null));
    }

    @Test
    void shouldMapDomainToDto() {
        OrderItem domain = new OrderItem(1L, 2L, 3L, 4, BigDecimal.TEN);

        OrderItemDto dto = mapper.toDto(domain);

        assertNotNull(dto);
        assertEquals(domain.getId(), dto.id());
        assertEquals(domain.getOrderId(), dto.orderId());
        assertEquals(domain.getProductId(), dto.productId());
        assertEquals(domain.getQuantity(), dto.quantity());
        assertEquals(domain.getPrice(), dto.price());
    }

    @Test
    void shouldMapEntityToDto() {
        OrderEntity order = new OrderEntity();
        order.setId(2L);

        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(1L);
        entity.setOrder(order);
        entity.setProductId(3L);
        entity.setQuantity(4);
        entity.setPrice(BigDecimal.TEN);

        OrderItemDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.id());
        assertEquals(order.getId(), dto.orderId());
        assertEquals(entity.getProductId(), dto.productId());
        assertEquals(entity.getQuantity(), dto.quantity());
        assertEquals(entity.getPrice(), dto.price());
    }

    @Test
    void shouldMapDtoToDomain() {
        OrderItemDto dto = new OrderItemDto(1L, 2L, 3L, 4, BigDecimal.TEN);

        OrderItem domain = mapper.toDomain(dto);

        assertNotNull(domain);
        assertEquals(dto.id(), domain.getId());
        assertEquals(dto.orderId(), domain.getOrderId());
        assertEquals(dto.productId(), domain.getProductId());
        assertEquals(dto.quantity(), domain.getQuantity());
        assertEquals(dto.price(), domain.getPrice());
    }

    @Test
    void shouldReturnNullWhenDtoIsNull() {
        assertNull(mapper.toDto((OrderItem) null));
        assertNull(mapper.toDomain((OrderItemDto) null));
    }

    @Test
    void shouldMapDtoToEntity() {
        OrderItemDto dto = new OrderItemDto(1L, 2L, 3L, 4, BigDecimal.TEN);
        OrderEntity order = new OrderEntity();

        OrderItemEntity entity = mapper.toEntity(dto, order);

        assertNotNull(entity);
        assertEquals(dto.id(), entity.getId());
        assertEquals(dto.productId(), entity.getProductId());
        assertEquals(dto.quantity(), entity.getQuantity());
        assertEquals(dto.price(), entity.getPrice());
        assertEquals(order, entity.getOrder());
    }

    @Test
    void shouldUpdateEntityFromDto() {
        OrderItemDto dto = new OrderItemDto(1L, 2L, 3L, 4, BigDecimal.TEN);
        OrderItemEntity entity = new OrderItemEntity();
        OrderEntity order = new OrderEntity();

        mapper.updateFromDto(dto, entity, order);

        assertEquals(dto.productId(), entity.getProductId());
        assertEquals(dto.quantity(), entity.getQuantity());
        assertEquals(dto.price(), entity.getPrice());
        assertEquals(order, entity.getOrder());
    }

    @Test
    void shouldDoNothingWhenUpdatingWithNulls() {
        mapper.updateFromDto(null, null, null);
        // Just ensure no exception is thrown
    }
}
