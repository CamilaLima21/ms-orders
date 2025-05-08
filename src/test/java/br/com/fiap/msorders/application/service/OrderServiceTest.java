package br.com.fiap.msorders.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.fiap.msorders.application.dto.OrderDto;
import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.application.mapper.OrderMapper;
import br.com.fiap.msorders.domain.enums.OrderStatus;
import br.com.fiap.msorders.domain.model.Order;
import br.com.fiap.msorders.domain.model.OrderItem;
import br.com.fiap.msorders.infrastructure.integration.service.ClientServiceClient;
import br.com.fiap.msorders.infrastructure.integration.service.ProductServiceClient;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.repository.OrderRepository;
import br.com.fiap.msorders.infrastructure.web.exceptions.ResourceNotFoundException;

class OrderServiceTest {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderMapper mapper;

    @Mock
    private ClientServiceClient clientServiceClient;

    @Mock
    private ProductServiceClient productServiceClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, "sku-123", 2, BigDecimal.TEN);
        List<OrderItemDto> itemsDto = List.of(itemDto);
        OrderDto dto = new OrderDto(1L, 5L, BigDecimal.TEN, null, null, null, itemsDto);

        OrderItem item = new OrderItem(1L, 1L, "sku-123", 2, BigDecimal.TEN);
        Order domain = new Order(1L, 5L, BigDecimal.TEN, OrderStatus.CREATED, LocalDateTime.now(), LocalDateTime.now(), List.of(item));
        OrderEntity entity = new OrderEntity();

        when(mapper.toDomain(dto)).thenReturn(domain);
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);
        when(mapper.toDto(domain)).thenReturn(dto);

        doNothing().when(clientServiceClient).validateClientExists(5L);
        doNothing().when(productServiceClient).validateSkus(List.of("sku-123"));

        OrderDto created = service.createOrder(dto);

        assertNotNull(created);
        assertEquals(dto.clientId(), created.clientId());
        verify(repository).save(entity);
        verify(clientServiceClient).validateClientExists(5L);
        verify(productServiceClient).validateSkus(List.of("sku-123"));
    }

    @Test
    void shouldThrowExceptionWhenClientIdIsInvalid() {
        OrderDto dto = new OrderDto(1L, 0L, BigDecimal.TEN, OrderStatus.CREATED, null, null, List.of());
        assertThrows(IllegalArgumentException.class, () -> service.createOrder(dto));
    }

    @Test
    void shouldThrowExceptionWhenItemsAreEmpty() {
        OrderDto dto = new OrderDto(1L, 5L, BigDecimal.TEN, OrderStatus.CREATED, null, null, List.of());
        doNothing().when(clientServiceClient).validateClientExists(5L);
        assertThrows(IllegalArgumentException.class, () -> service.createOrder(dto));
    }

    @Test
    void shouldFindOrderById() throws ResourceNotFoundException {
        long orderId = 1L;
        OrderEntity entity = new OrderEntity();
        Order domain = new Order(orderId, 5L, BigDecimal.TEN, OrderStatus.CREATED, LocalDateTime.now(), LocalDateTime.now(), List.of());
        OrderDto dto = new OrderDto(orderId, 5L, BigDecimal.TEN, OrderStatus.CREATED, LocalDateTime.now(), LocalDateTime.now(), List.of());

        when(repository.findById(orderId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);
        when(mapper.toDto(domain)).thenReturn(dto);

        OrderDto result = service.findOrderById(orderId);
        assertEquals(dto, result);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundById() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findOrderById(1L));
    }

    @Test
    void shouldReturnAllOrders() {
        OrderEntity entity = new OrderEntity();
        Order domain = new Order(1L, 5L, BigDecimal.TEN, OrderStatus.CREATED, LocalDateTime.now(), LocalDateTime.now(), List.of());
        OrderDto dto = new OrderDto(1L, 5L, BigDecimal.TEN, OrderStatus.CREATED, LocalDateTime.now(), LocalDateTime.now(), List.of());

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);
        when(mapper.toDto(domain)).thenReturn(dto);

        List<OrderDto> result = service.findAllOrders();
        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void shouldUpdateOrderSuccessfully() throws ResourceNotFoundException {
        long orderId = 1L;
        OrderEntity existingEntity = new OrderEntity();
        existingEntity.setOrderItems(new ArrayList<>()); // Corrigido: lista mutável

        OrderItemDto itemDto = new OrderItemDto(1L, 1L, "sku-123", 2, BigDecimal.TEN);
        List<OrderItemDto> itemsDto = List.of(itemDto);

        OrderDto dto = new OrderDto(orderId, 5L, BigDecimal.TEN, OrderStatus.CREATED, null, null, itemsDto);
        Order domain = new Order(orderId, 5L, BigDecimal.TEN, OrderStatus.CREATED, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>());
        OrderEntity savedEntity = new OrderEntity();

        when(repository.findById(orderId)).thenReturn(Optional.of(existingEntity));
        when(repository.save(any(OrderEntity.class))).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(domain);
        when(mapper.toDto(domain)).thenReturn(dto);

        OrderDto updated = service.updateOrder(orderId, dto);
        assertNotNull(updated);
        assertEquals(dto.status(), updated.status());
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentOrder() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateOrder(1L, mock(OrderDto.class)));
    }

    @Test
    void shouldDeleteOrderSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);
        boolean result = service.deleteOrder(1L);
        assertTrue(result);
        verify(repository).deleteById(1L);
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentOrder() {
        when(repository.existsById(1L)).thenReturn(false);
        boolean result = service.deleteOrder(1L);
        assertFalse(result);
    }
}
