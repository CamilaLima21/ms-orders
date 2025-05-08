package br.com.fiap.msorders.application.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.fiap.msorders.application.dto.OrderDto;
import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.domain.enums.OrderStatus;
import br.com.fiap.msorders.domain.model.Order;
import br.com.fiap.msorders.domain.model.OrderItem;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderItemEntity;

class OrderMapperTest {

    private OrderItemMapper orderItemMapper;
    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        orderItemMapper = mock(OrderItemMapper.class);
        orderMapper = new OrderMapper(orderItemMapper);
    }

    @Test
    void shouldMapDtoToDomain() {
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, "SKU-10", 2, BigDecimal.TEN);
        OrderItem orderItem = new OrderItem(1L, 1L, "SKU-10", 2, BigDecimal.TEN);

        when(orderItemMapper.toDomain(itemDto)).thenReturn(orderItem);

        OrderDto dto = new OrderDto(
            1L,
            5L,
            BigDecimal.valueOf(100),
            OrderStatus.CREATED,
            LocalDateTime.now(),
            LocalDateTime.now(),
            List.of(itemDto)
        );

        Order domain = orderMapper.toDomain(dto);

        assertEquals(dto.id(), domain.getId());
        assertEquals(dto.clientId(), domain.getClientId());
        assertEquals(dto.total(), domain.getTotal());
        assertEquals(dto.status(), domain.getStatus());
        assertEquals(dto.createdAt(), domain.getCreatedAt());
        assertEquals(dto.updatedAt(), domain.getUpdatedAt());
        assertEquals(1, domain.getItems().size());
        verify(orderItemMapper).toDomain(itemDto);
    }

    @Test
    void shouldMapEntityToDomain() {
        OrderItemEntity itemEntity = new OrderItemEntity(1L, null, "SKU-10", 2, BigDecimal.TEN);
        OrderItem orderItem = new OrderItem(1L, 1L, "SKU-10", 2, BigDecimal.TEN);

        when(orderItemMapper.toDomain(itemEntity)).thenReturn(orderItem);

        OrderEntity entity = new OrderEntity();
        entity.setId(1L);
        entity.setClientId(5L);
        entity.setTotal(BigDecimal.valueOf(100));
        entity.setStatus(OrderStatus.CREATED);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setOrderItems(List.of(itemEntity));

        Order domain = orderMapper.toDomain(entity);

        assertEquals(entity.getId(), domain.getId());
        assertEquals(entity.getClientId(), domain.getClientId());
        assertEquals(entity.getTotal(), domain.getTotal());
        assertEquals(entity.getStatus(), domain.getStatus());
        assertEquals(entity.getCreatedAt(), domain.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), domain.getUpdatedAt());
        assertEquals(1, domain.getItems().size());
        verify(orderItemMapper).toDomain(itemEntity);
    }

    @Test
    void shouldMapDomainToDto() {
        OrderItem item = new OrderItem(1L, 1L, "SKU-10", 2, BigDecimal.TEN);
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, "SKU-10", 2, BigDecimal.TEN);

        when(orderItemMapper.toDto(item)).thenReturn(itemDto);

        Order domain = new Order(
            1L,
            5L,
            BigDecimal.valueOf(100),
            OrderStatus.CREATED,
            LocalDateTime.now(),
            LocalDateTime.now(),
            List.of(item)
        );

        OrderDto dto = orderMapper.toDto(domain);

        assertEquals(domain.getId(), dto.id());
        assertEquals(domain.getClientId(), dto.clientId());
        assertEquals(domain.getTotal(), dto.total());
        assertEquals(domain.getStatus(), dto.status());
        assertEquals(domain.getCreatedAt(), dto.createdAt());
        assertEquals(domain.getUpdatedAt(), dto.updatedAt());
        assertEquals(1, dto.items().size());
        verify(orderItemMapper).toDto(item);
    }


    @Test
    void shouldMapDomainToEntity() {
        OrderItem item = new OrderItem(1L, 1L, "SKU-10", 2, BigDecimal.TEN);
        OrderItemEntity itemEntity = new OrderItemEntity(1L, null, "SKU-10", 2, BigDecimal.TEN);

        when(orderItemMapper.toEntity(eq(item), any(OrderEntity.class))).thenAnswer(invocation -> {
            OrderEntity orderRef = invocation.getArgument(1);
            itemEntity.setOrder(orderRef);
            return itemEntity;
        });

        LocalDateTime now = LocalDateTime.now();

        Order domain = new Order(
            1L,
            5L,
            BigDecimal.valueOf(100),
            OrderStatus.CREATED,
            now,
            now,
            List.of(item)
        );

        OrderEntity entity = orderMapper.toEntity(domain);

        assertEquals(domain.getId(), entity.getId());
        assertEquals(domain.getClientId(), entity.getClientId());
        assertEquals(domain.getTotal(), entity.getTotal());
        assertEquals(domain.getStatus(), entity.getStatus());
        assertEquals(domain.getCreatedAt(), entity.getCreatedAt());
        assertEquals(domain.getUpdatedAt(), entity.getUpdatedAt());
        assertEquals(1, entity.getOrderItems().size());

        verify(orderItemMapper).toEntity(eq(item), any(OrderEntity.class));
    }
}
