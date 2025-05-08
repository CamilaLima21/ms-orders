package br.com.fiap.msorders.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.application.mapper.OrderItemMapper;
import br.com.fiap.msorders.domain.model.OrderItem;
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
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateOrderItemSuccessfully() throws ResourceNotFoundException {
        // Arrange
        OrderItemDto inputDto = new OrderItemDto(1L, 100L, "SKU123", 2, BigDecimal.valueOf(29.99));

        // Criando a ordem de teste com o id necessário
        OrderEntity mockOrder = new OrderEntity();
        mockOrder.setId(100L);  // Defina o id da ordem

        OrderItem domain = new OrderItem(1L, 100L, "SKU123", 2, BigDecimal.valueOf(29.99));
        OrderItemEntity entity = new OrderItemEntity(mockOrder, "SKU123", 2, BigDecimal.valueOf(29.99));  // Instanciando com o construtor correto
        OrderItemDto expectedDto = new OrderItemDto(1L, 100L, "SKU123", 2, BigDecimal.valueOf(29.99));

        // Mocking dos repositórios e mapper
        when(orderRepository.findById(inputDto.orderId())).thenReturn(Optional.of(mockOrder));
        when(orderItemMapper.toDomain(inputDto)).thenReturn(domain);
        when(orderItemMapper.toEntity(domain, mockOrder)).thenReturn(entity);
        when(orderItemRepository.save(entity)).thenReturn(entity);
        when(orderItemMapper.toDto(entity)).thenReturn(expectedDto);

        // Act
        OrderItemDto result = orderItemService.create(inputDto);

//        // Assert
//        assertNotNull(result, "The result should not be null");  // Verificando se o resultado não é null
//        assertEquals(expectedDto.id(), result.id(), "The ID should match");
//        assertEquals(expectedDto.productSku(), result.productSku(), "The product SKU should match");
//        assertEquals(expectedDto.quantity(), result.quantity(), "The quantity should match");
//        assertEquals(expectedDto.price(), result.price(), "The price should match");
//
//        // Verifica se o save foi chamado
//        verify(orderItemRepository, times(1)).save(entity); 
//        verify(orderItemMapper, times(1)).toDto(entity); 
    }




    @Test
    void shouldThrowExceptionWhenCreatingOrderItemWithInvalidQuantity() {
        OrderItemDto dto = new OrderItemDto(1L, 100L, "SKU123", 0, BigDecimal.valueOf(29.99));
        assertThrows(IllegalArgumentException.class, () -> orderItemService.create(dto));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundWhileCreatingOrderItem() {
        OrderItemDto dto = new OrderItemDto(1L, 100L, "SKU123", 2, BigDecimal.valueOf(29.99));
        when(orderRepository.findById(dto.orderId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderItemService.create(dto));
    }

    @Test
    void shouldFindAllOrderItems() {
        List<OrderItemEntity> entities = List.of(mock(OrderItemEntity.class));
        OrderItemDto dto = new OrderItemDto(1L, 100L, "SKU123", 2, BigDecimal.valueOf(29.99));

        when(orderItemRepository.findAll()).thenReturn(entities);
        when(orderItemMapper.toDtoList(entities)).thenReturn(List.of(dto));

        List<OrderItemDto> result = orderItemService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SKU123", result.get(0).productSku());
    }

    @Test
    void shouldFindOrderItemById() throws ResourceNotFoundException {
        long id = 1L;
        OrderItemEntity entity = mock(OrderItemEntity.class);
        OrderItemDto dto = new OrderItemDto(id, 100L, "SKU123", 2, BigDecimal.valueOf(29.99));

        when(orderItemRepository.findById(id)).thenReturn(Optional.of(entity));
        when(orderItemMapper.toDto(entity)).thenReturn(dto);

        OrderItemDto result = orderItemService.findById(id);

        assertNotNull(result);
        assertEquals("SKU123", result.productSku());
    }

    @Test
    void shouldThrowExceptionWhenOrderItemNotFoundById() {
        when(orderItemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderItemService.findById(1L));
    }

    @Test
    void shouldUpdateOrderItemSuccessfully() throws ResourceNotFoundException {
        long id = 1L;
        OrderItemDto dto = new OrderItemDto(id, 100L, "SKU123", 3, BigDecimal.valueOf(29.99));
        OrderItemEntity entity = mock(OrderItemEntity.class);
        OrderEntity order = mock(OrderEntity.class);

        when(orderItemRepository.findById(id)).thenReturn(Optional.of(entity));
        when(orderRepository.findById(dto.orderId())).thenReturn(Optional.of(order));
        doNothing().when(orderItemMapper).updateFromDto(dto, entity, order);
        when(orderItemRepository.save(entity)).thenReturn(entity);
        when(orderItemMapper.toDto(entity)).thenReturn(dto);

        OrderItemDto result = orderItemService.update(id, dto);

        assertNotNull(result);
        assertEquals("SKU123", result.productSku());
    }

    @Test
    void shouldThrowExceptionWhenOrderItemNotFoundForUpdate() {
        long id = 1L;
        OrderItemDto dto = new OrderItemDto(id, 100L, "SKU123", 3, BigDecimal.valueOf(29.99));
        when(orderItemRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> orderItemService.update(id, dto));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundForUpdate() {
        long id = 1L;
        OrderItemDto dto = new OrderItemDto(id, 100L, "SKU123", 3, BigDecimal.valueOf(29.99));
        OrderItemEntity entity = mock(OrderItemEntity.class);

        when(orderItemRepository.findById(id)).thenReturn(Optional.of(entity));
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
