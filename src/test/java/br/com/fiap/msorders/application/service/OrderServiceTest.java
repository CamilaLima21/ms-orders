package br.com.fiap.msorders.application.service;

import br.com.fiap.msorders.application.dto.OrderDto;
import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.application.mapper.OrderMapper;
import br.com.fiap.msorders.domain.enums.OrderStatus;
import br.com.fiap.msorders.domain.model.Order;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.repository.OrderRepository;
import br.com.fiap.msorders.infrastructure.web.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderMapper mapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        OrderDto dto = new OrderDto(1L, 5L, BigDecimal.TEN, OrderStatus.CREATED, null, null, List.of());
        Order domain = mock(Order.class);
        OrderEntity entity = mock(OrderEntity.class);

        when(mapper.toDomain(dto)).thenReturn(domain);
        when(mapper.toEntity(domain)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);
        when(mapper.toDto(domain)).thenReturn(dto);

        OrderDto created = service.createOrder(dto);

        assertNotNull(created);
        assertEquals(dto.clientId(), created.clientId());
        verify(repository).save(entity);
    }

    @Test
    void shouldThrowExceptionWhenClientIdIsInvalid() {
        OrderDto dto = new OrderDto(1L, 0L, BigDecimal.TEN, OrderStatus.CREATED, null, null, List.of());
        assertThrows(IllegalArgumentException.class, () -> service.createOrder(dto));
    }

    @Test
    void shouldFindOrderById() throws ResourceNotFoundException {
        long orderId = 1L;
        OrderEntity entity = mock(OrderEntity.class);
        Order domain = mock(Order.class);
        OrderDto dto = new OrderDto(orderId, 5L, BigDecimal.TEN, OrderStatus.CREATED, null, null, List.of());

        when(repository.findById(orderId)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);
        when(mapper.toDto(domain)).thenReturn(dto);

        OrderDto found = service.findOrderById(orderId);

        assertNotNull(found);
        assertEquals(orderId, found.id());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findOrderById(1L));
    }

    @Test
    void shouldReturnAllOrders() {
        // Simulando uma lista de entidades de pedidos
        List<OrderEntity> entities = List.of(mock(OrderEntity.class));
        
        // Simulando os objetos de domínio e DTO
        Order domain = mock(Order.class);
        OrderDto dto = new OrderDto(1L, 5L, BigDecimal.TEN, OrderStatus.CREATED, null, null, List.of());

        // Configuração dos mocks
        when(repository.findAll()).thenReturn(entities);
        
        // Especificar explicitamente que toDomain é chamado com OrderEntity
        when(mapper.toDomain(any(OrderEntity.class))).thenReturn(domain);
        
        // Configurar o mapeamento para OrderDto
        when(mapper.toDto(domain)).thenReturn(dto);

        // Chamar o método para testar
        List<OrderDto> result = service.findAllOrders();

        // Verificar o tamanho da lista resultante
        assertEquals(1, result.size());
        
        // Verificar se o método findAll() foi chamado no repositório
        verify(repository).findAll();
    }


    @Test
    void shouldUpdateOrderSuccessfully() throws ResourceNotFoundException {
        long id = 1L;
        OrderDto dto = new OrderDto(id, 5L, BigDecimal.valueOf(100), OrderStatus.PAID, null, null,
                List.of(new OrderItemDto(1L, id, 3L, 2, BigDecimal.TEN)));

        OrderEntity existing = new OrderEntity();
        existing.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any())).thenReturn(existing);
        when(mapper.toDomain(existing)).thenReturn(mock(Order.class));
        when(mapper.toDto(any())).thenReturn(dto);

        OrderDto updated = service.updateOrder(id, dto);

        assertEquals(id, updated.id());
        verify(repository).save(existing);
    }

    @Test
    void shouldDeleteOrderSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);
        boolean deleted = service.deleteOrder(1L);
        assertTrue(deleted);
        verify(repository).deleteById(1L);
    }

    @Test
    void shouldReturnFalseWhenOrderToDeleteNotExists() {
        when(repository.existsById(1L)).thenReturn(false);
        boolean deleted = service.deleteOrder(1L);
        assertFalse(deleted);
        verify(repository, never()).deleteById(1L);
    }
}
