package br.com.fiap.msorders.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
import org.springframework.http.ResponseEntity;

import br.com.fiap.msorders.application.dto.OrderDto;
import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.application.mapper.OrderMapper;
import br.com.fiap.msorders.domain.enums.OrderStatus;
import br.com.fiap.msorders.domain.model.Order;
import br.com.fiap.msorders.infrastructure.integration.dto.CreditCardPaymentResponseDto;
import br.com.fiap.msorders.infrastructure.integration.dto.QRCodePaymentResponseDto;
import br.com.fiap.msorders.infrastructure.integration.dto.StatusDto;
import br.com.fiap.msorders.infrastructure.integration.dto.StockDto;
import br.com.fiap.msorders.infrastructure.integration.dto.TokenResponseDto;
import br.com.fiap.msorders.infrastructure.integration.service.ClientServiceClient;
import br.com.fiap.msorders.infrastructure.integration.service.PaymentServiceClient;
import br.com.fiap.msorders.infrastructure.integration.service.ProductServiceClient;
import br.com.fiap.msorders.infrastructure.integration.service.StockServiceClient;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderItemEntity;
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
    private StockServiceClient stockServiceClient;

    @Mock
    private ClientServiceClient clientServiceClient;

    @Mock
    private ProductServiceClient productServiceClient;
    
    @Mock
    private PaymentServiceClient paymentServiceClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        OrderItemDto itemDto = new OrderItemDto(0L, 0L, "sku-123", 2, BigDecimal.TEN);
        List<OrderItemDto> itemsDto = List.of(itemDto);
        OrderDto dto = new OrderDto(0L, 5L, BigDecimal.TEN, null, null, null, itemsDto);

        OrderEntity entityToSave = new OrderEntity();
        entityToSave.setClientId(5L);
        entityToSave.setStatus(OrderStatus.CREATED);
        entityToSave.setTotal(BigDecimal.TEN);
        entityToSave.setCreatedAt(LocalDateTime.now());
        entityToSave.setUpdatedAt(LocalDateTime.now());

        OrderItemEntity itemEntity = new OrderItemEntity();
        itemEntity.setProductSku("sku-123");
        itemEntity.setQuantity(2);
        itemEntity.setPrice(BigDecimal.TEN);
        entityToSave.addOrderItem(itemEntity);

        OrderEntity savedEntity = new OrderEntity();
        savedEntity.setId(1L);
        savedEntity.setClientId(5L);
        savedEntity.setTotal(BigDecimal.TEN);
        savedEntity.setStatus(OrderStatus.CREATED);
        savedEntity.setCreatedAt(LocalDateTime.now());
        savedEntity.setUpdatedAt(LocalDateTime.now());
        savedEntity.setOrderItems(List.of(itemEntity));

        Order domain = new Order(1L, 5L, BigDecimal.TEN, OrderStatus.CREATED, savedEntity.getCreatedAt(), savedEntity.getUpdatedAt(), new ArrayList<>());
        OrderDto expectedDto = new OrderDto(1L, 5L, BigDecimal.TEN, OrderStatus.CREATED, savedEntity.getCreatedAt(), savedEntity.getUpdatedAt(), itemsDto);

        doNothing().when(clientServiceClient).validateClientExists(5L);
        doNothing().when(productServiceClient).validateSkus(List.of("sku-123"));

        when(repository.save(any(OrderEntity.class))).thenReturn(savedEntity);
        when(mapper.toDomain(savedEntity)).thenReturn(domain);
        when(mapper.toDto(domain)).thenReturn(expectedDto);
        when(stockServiceClient.searchStock("sku-123")).thenReturn(new StockDto(null, "sku-123", 10)); // Corrected mock

        OrderDto result = service.createOrder(dto);

        assertNotNull(result);
        assertEquals(expectedDto.clientId(), result.clientId());
        assertEquals(expectedDto.total(), result.total());
        assertEquals(expectedDto.status(), result.status());

        verify(clientServiceClient).validateClientExists(5L);
        verify(productServiceClient).validateSkus(List.of("sku-123"));
        verify(repository).save(any(OrderEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenClientIdIsInvalid() {
        OrderDto dto = new OrderDto(0L, 0L, BigDecimal.TEN, null, null, null, List.of());
        assertThrows(IllegalArgumentException.class, () -> service.createOrder(dto));
    }

    @Test
    void shouldThrowExceptionWhenItemsAreEmpty() {
        OrderDto dto = new OrderDto(0L, 5L, BigDecimal.TEN, null, null, null, List.of());
        doNothing().when(clientServiceClient).validateClientExists(5L);
        assertThrows(IllegalArgumentException.class, () -> service.createOrder(dto));
    }

    @Test
    void shouldFindOrderById() throws ResourceNotFoundException {
        long orderId = 1L;
        LocalDateTime now = LocalDateTime.now();
        OrderEntity entity = new OrderEntity();
        Order domain = new Order(orderId, 5L, BigDecimal.TEN, OrderStatus.CREATED, now, now, List.of());
        OrderDto dto = new OrderDto(orderId, 5L, BigDecimal.TEN, OrderStatus.CREATED, now, now, List.of());

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
    void testProcessPayment() throws Exception {
        // Arrange
        long orderId = 1L;
        String paymentMethod = "CARD";

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        orderEntity.setClientId(123L);
        orderEntity.setTotal(BigDecimal.valueOf(100.00));
        orderEntity.setStatus(OrderStatus.CREATED);

        OrderItemEntity item = new OrderItemEntity();
        item.setProductSku("SKU123");
        item.setQuantity(1);
        item.setPrice(BigDecimal.valueOf(100.00));
        orderEntity.setOrderItems(List.of(item));

        CreditCardPaymentResponseDto paymentResponse = new CreditCardPaymentResponseDto("1", "APPROVED", "Pagamento aprovado com sucesso.", "123456", 100.0, "BRL", "1");
        
        TokenResponseDto tokenResponse = new TokenResponseDto("access_token", "token_type", 3600, "oob");
        when(repository.findById(orderId)).thenReturn(Optional.of(orderEntity));
  
		when(paymentServiceClient.generateToken(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(tokenResponse);
        when(paymentServiceClient.processCreditCardPayment(any(), anyString()))
            .thenReturn(paymentResponse);
        when(repository.save(any(OrderEntity.class))).thenReturn(orderEntity);

        OrderDto result = service.processPayment(orderId, paymentMethod);

        assertNull(result);
    }
    
    @Test
    void testProcessPaymentPIX() throws Exception {
        // Arrange
        long orderId = 1L;
        String paymentMethod = "PIX";

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(orderId);
        orderEntity.setClientId(123L);
        orderEntity.setTotal(BigDecimal.valueOf(100.00));
        orderEntity.setStatus(OrderStatus.CREATED);

        OrderItemEntity item = new OrderItemEntity();
        item.setProductSku("SKU123");
        item.setQuantity(1);
        item.setPrice(BigDecimal.valueOf(100.00));
        orderEntity.setOrderItems(List.of(item));

        QRCodePaymentResponseDto qrCodePaymentResponse = new QRCodePaymentResponseDto(
            "seller_id", "hash_qr_code", "APPROVED", null
        );
        
        StatusDto statusDto = new StatusDto("seller_id", "hash_qr_code", "APPROVED", "1", 10.10, null, null);
        
        TokenResponseDto tokenResponse = new TokenResponseDto("access_token", "token_type", 3600, "oob");
        when(repository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(paymentServiceClient.generateToken(anyString(), anyString(), anyString(), anyString()))
            .thenReturn(tokenResponse);
        when(paymentServiceClient.generateQRCodePayment(any(), anyString()))
            .thenReturn(qrCodePaymentResponse); // Return QRCodePaymentResponseDto directly
        when(repository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(paymentServiceClient.getStatus(anyString(), anyString()))
        .thenReturn(ResponseEntity.ok(statusDto)); 

        OrderDto result = service.processPayment(orderId, paymentMethod);
        assertNull(result);
    }

    @Test
    void shouldReturnAllOrders() {
        LocalDateTime now = LocalDateTime.now();
        OrderEntity entity = new OrderEntity();
        Order domain = new Order(1L, 5L, BigDecimal.TEN, OrderStatus.CREATED, now, now, List.of());
        OrderDto dto = new OrderDto(1L, 5L, BigDecimal.TEN, OrderStatus.CREATED, now, now, List.of());

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
        LocalDateTime now = LocalDateTime.now();

        OrderEntity existingEntity = new OrderEntity();
        existingEntity.setOrderItems(new ArrayList<>());

        OrderItemDto itemDto = new OrderItemDto(1L, orderId, "sku-123", 2, BigDecimal.TEN);
        List<OrderItemDto> itemsDto = List.of(itemDto);

        OrderDto dto = new OrderDto(orderId, 5L, BigDecimal.TEN, OrderStatus.CREATED, now, now, itemsDto);
        Order domain = new Order(orderId, 5L, BigDecimal.TEN, OrderStatus.CREATED, now, now, new ArrayList<>());
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
        assertThrows(ResourceNotFoundException.class, () -> service.updateOrder(1L, new OrderDto(1L, 5L, BigDecimal.TEN, OrderStatus.CREATED, LocalDateTime.now(), LocalDateTime.now(), List.of())));
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
