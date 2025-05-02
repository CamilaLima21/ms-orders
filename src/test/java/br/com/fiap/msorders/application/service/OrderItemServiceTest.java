package br.com.fiap.msorders.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.application.mapper.OrderItemMapper;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderItemEntity;
import br.com.fiap.msorders.infrastructure.persistence.repository.OrderItemRepository;
import br.com.fiap.msorders.infrastructure.persistence.repository.OrderRepository;
import br.com.fiap.msorders.infrastructure.web.exceptions.ResourceNotFoundException;

class OrderItemServiceTest {

    @InjectMocks
    private OrderItemService orderItemService;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemMapper orderItemMapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateOrderItemSuccessfully() throws ResourceNotFoundException {
        OrderItemDto dto = new OrderItemDto(1L, 1L, 3L, 5, null);
        OrderEntity order = mock(OrderEntity.class);
        OrderItemEntity savedEntity = mock(OrderItemEntity.class);
        OrderItemDto savedDto = new OrderItemDto(1L, 1L, 3L, 5, null);

        when(orderRepository.findById(dto.orderId())).thenReturn(Optional.of(order));
        when(orderItemMapper.toEntity(dto, order)).thenReturn(savedEntity);
        when(orderItemRepository.save(savedEntity)).thenReturn(savedEntity);
        when(orderItemMapper.toDto(savedEntity)).thenReturn(savedDto);

        OrderItemDto result = orderItemService.create(dto);

        assertNotNull(result);
        assertEquals(dto.orderId(), result.orderId());
        verify(orderItemRepository).save(savedEntity);
    }

    @Test
    void shouldThrowExceptionWhenCreatingOrderItemWithInvalidQuantity() {
        OrderItemDto dto = new OrderItemDto(1L, 1L, 3L, 0, null); // Invalid quantity

        assertThrows(IllegalArgumentException.class, () -> orderItemService.create(dto));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundWhileCreatingOrderItem() {
        OrderItemDto dto = new OrderItemDto(1L, 1L, 3L, 5, null);

        when(orderRepository.findById(dto.orderId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemService.create(dto));
    }

    @Test
    void shouldFindAllOrderItems() {
        List<OrderItemEntity> entities = List.of(mock(OrderItemEntity.class));
        OrderItemDto dto = new OrderItemDto(1L, 1L, 3L, 5, null);

        when(orderItemRepository.findAll()).thenReturn(entities);
        when(orderItemMapper.toDtoList(entities)).thenReturn(List.of(dto));

        List<OrderItemDto> result = orderItemService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderItemRepository).findAll();
    }

    @Test
    void shouldFindOrderItemById() throws ResourceNotFoundException {
        long id = 1L;
        OrderItemEntity entity = mock(OrderItemEntity.class);
        OrderItemDto dto = new OrderItemDto(id, 1L, 3L, 5, null);

        when(orderItemRepository.findById(id)).thenReturn(Optional.of(entity));
        when(orderItemMapper.toDto(entity)).thenReturn(dto);

        OrderItemDto result = orderItemService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        verify(orderItemRepository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenOrderItemNotFoundById() {
        when(orderItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemService.findById(1L));
    }

    @Test
    void shouldUpdateOrderItemSuccessfully() throws ResourceNotFoundException {
        long id = 1L;
        OrderItemDto dto = new OrderItemDto(id, 1L, 3L, 5, null);
        OrderItemEntity existingEntity = mock(OrderItemEntity.class);
        OrderEntity order = mock(OrderEntity.class);

        when(orderItemRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(orderRepository.findById(dto.orderId())).thenReturn(Optional.of(order));

        // Usar doNothing() porque o método updateFromDto é void
        doNothing().when(orderItemMapper).updateFromDto(dto, existingEntity, order);
        
        when(orderItemRepository.save(existingEntity)).thenReturn(existingEntity);
        when(orderItemMapper.toDto(existingEntity)).thenReturn(dto);

        OrderItemDto updated = orderItemService.update(id, dto);

        assertNotNull(updated);
        assertEquals(id, updated.id());
        verify(orderItemRepository).save(existingEntity);
    }


    @Test
    void shouldThrowExceptionWhenOrderItemNotFoundForUpdate() {
        long id = 1L;
        OrderItemDto dto = new OrderItemDto(id, 1L, 3L, 5, null);

        when(orderItemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemService.update(id, dto));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundForUpdate() {
        long id = 1L;
        OrderItemDto dto = new OrderItemDto(id, 1L, 3L, 5, null);
        OrderItemEntity existingEntity = mock(OrderItemEntity.class);

        when(orderItemRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(orderRepository.findById(dto.orderId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemService.update(id, dto));
    }

    @Test
    void shouldDeleteOrderItemSuccessfully() throws ResourceNotFoundException {
        long id = 1L;
        OrderItemEntity entity = mock(OrderItemEntity.class);

        when(orderItemRepository.findById(id)).thenReturn(Optional.of(entity));

        String result = orderItemService.delete(id);

        assertEquals("Order item deleted successfully!", result);
        verify(orderItemRepository).delete(entity);
    }

    @Test
    void shouldThrowExceptionWhenOrderItemNotFoundForDeletion() {
        when(orderItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderItemService.delete(1L));
    }
}
