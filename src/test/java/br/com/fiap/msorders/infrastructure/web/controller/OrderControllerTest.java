package br.com.fiap.msorders.infrastructure.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.fiap.msorders.application.dto.OrderDto;
import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.application.service.OrderService;
import br.com.fiap.msorders.domain.enums.OrderStatus;
import br.com.fiap.msorders.infrastructure.integration.service.ClientServiceClient;
import br.com.fiap.msorders.infrastructure.integration.service.ProductServiceClient;
import br.com.fiap.msorders.infrastructure.integration.service.StockServiceClient;
import br.com.fiap.msorders.infrastructure.web.exceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@TestPropertySource(properties = {
    "ms.clients.url=localhost:8081",
    "ms.products.url=localhost:8082"
})
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;
    
    @MockBean
    private ClientServiceClient clientServiceClient;

    @MockBean
    private ProductServiceClient productServiceClient;

    @MockBean
    private StockServiceClient stockServiceClient;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void shouldCreateOrder() throws Exception {
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, "SKU123", 5, BigDecimal.valueOf(100));
        LocalDateTime now = LocalDateTime.now();
        OrderDto orderDto = new OrderDto(0L, 1L, BigDecimal.valueOf(500), OrderStatus.CREATED, now, now, List.of(itemDto));
        OrderDto createdOrder = new OrderDto(1L, 1L, BigDecimal.valueOf(500), OrderStatus.CREATED, now, now, List.of(itemDto));

        when(orderService.createOrder(any(OrderDto.class))).thenReturn(createdOrder);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.clientId").value(1L))
            .andExpect(jsonPath("$.total").value(500))
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void shouldGetOrderById() throws Exception {
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, "SKU123", 5, BigDecimal.valueOf(100));
        LocalDateTime now = LocalDateTime.now();
        OrderDto orderDto = new OrderDto(1L, 1L, BigDecimal.valueOf(500), OrderStatus.CREATED, now, now, List.of(itemDto));

        when(orderService.findOrderById(1L)).thenReturn(orderDto);

        mockMvc.perform(get("/orders/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.clientId").value(1L))
            .andExpect(jsonPath("$.status").value("CREATED"))
            .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    void shouldReturnNotFoundWhenOrderNotExist() throws Exception {
        when(orderService.findOrderById(999L)).thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(get("/orders/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllOrders() throws Exception {
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, "SKU123", 5, BigDecimal.valueOf(100));
        LocalDateTime now = LocalDateTime.now();
        OrderDto order1 = new OrderDto(1L, 1L, BigDecimal.valueOf(500), OrderStatus.CREATED, now, now, List.of(itemDto));
        OrderDto order2 = new OrderDto(2L, 2L, BigDecimal.valueOf(300), OrderStatus.CREATED, now, now, List.of(itemDto));

        when(orderService.findAllOrders()).thenReturn(List.of(order1, order2));

        mockMvc.perform(get("/orders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void shouldUpdateOrder() throws Exception {
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, "SKU123", 3, BigDecimal.valueOf(100));
        LocalDateTime now = LocalDateTime.now();
        OrderDto inputOrder = new OrderDto(0L, 1L, BigDecimal.valueOf(300), OrderStatus.CREATED, now, now, List.of(itemDto));
        OrderDto updatedOrder = new OrderDto(1L, 1L, BigDecimal.valueOf(300), OrderStatus.CREATED, now, now, List.of(itemDto));

        when(orderService.updateOrder(1L, inputOrder)).thenReturn(updatedOrder);

        mockMvc.perform(put("/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputOrder)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.total").value(300));
    }

    @Test
    void shouldReturnNotFoundWhenUpdateFails() throws Exception {
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, "SKU123", 3, BigDecimal.valueOf(100));
        LocalDateTime now = LocalDateTime.now();
        OrderDto inputOrder = new OrderDto(0L, 1L, BigDecimal.valueOf(300), OrderStatus.CREATED, now, now, List.of(itemDto));

        when(orderService.updateOrder(999L, inputOrder)).thenThrow(new ResourceNotFoundException("Order not found"));

        mockMvc.perform(put("/orders/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputOrder)))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteOrderSuccessfully() throws Exception {
        when(orderService.deleteOrder(1L)).thenReturn(true);

        mockMvc.perform(delete("/orders/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteFails() throws Exception {
        when(orderService.deleteOrder(999L)).thenReturn(false);

        mockMvc.perform(delete("/orders/999"))
            .andExpect(status().isNotFound());
    }
}

