package br.com.fiap.msorders.infrastructure.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;  // Importa o módulo para lidar com LocalDateTime

import br.com.fiap.msorders.application.dto.OrderDto;
import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.application.service.OrderService;
import br.com.fiap.msorders.infrastructure.web.exceptions.ResourceNotFoundException;

@SpringBootTest
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;  // Mock do serviço

    @InjectMocks
    private OrderController orderController;  // Injeção do mock no controlador

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Registra o módulo para lidar com LocalDateTime
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();  // Configura o MockMvc corretamente
    }

    @Test
    void shouldCreateOrder() throws Exception {
        // Arrange
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, 1L, 5, BigDecimal.valueOf(100));
        OrderDto orderDto = new OrderDto(0L, 1L, BigDecimal.valueOf(500), null, LocalDateTime.now(), LocalDateTime.now(), List.of(itemDto));
        OrderDto createdOrder = new OrderDto(1L, 1L, BigDecimal.valueOf(500), null, LocalDateTime.now(), LocalDateTime.now(), List.of(itemDto));

        when(orderService.createOrder(orderDto)).thenReturn(createdOrder);

        // Act & Assert
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.clientId").value(1L))
                .andExpect(jsonPath("$.total").value(500))
                .andExpect(jsonPath("$.item.size()").value(1));
    }


    @Test
    void shouldGetOrderById() throws Exception {
        // Arrange
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, 1L, 5, BigDecimal.valueOf(100));
        OrderDto orderDto = new OrderDto(1L, 1L, BigDecimal.valueOf(500), null, LocalDateTime.now(), LocalDateTime.now(), List.of(itemDto));
        when(orderService.findOrderById(1L)).thenReturn(orderDto);

        // Act & Assert
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.total").value(500))
                .andExpect(jsonPath("$.item.size()").value(1));
    }

    @Test
    void shouldReturnNotFoundWhenOrderNotExist() throws Exception {
        // Arrange
        when(orderService.findOrderById(999L)).thenThrow(new ResourceNotFoundException("Order not found"));

        // Act & Assert
        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllOrders() throws Exception {
        // Arrange
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, 1L, 5, BigDecimal.valueOf(100));
        OrderDto orderDto1 = new OrderDto(1L, 1L, BigDecimal.valueOf(500), null, LocalDateTime.now(), LocalDateTime.now(), List.of(itemDto));
        OrderDto orderDto2 = new OrderDto(2L, 1L, BigDecimal.valueOf(300), null, LocalDateTime.now(), LocalDateTime.now(), List.of(itemDto));
        when(orderService.findAllOrders()).thenReturn(List.of(orderDto1, orderDto2));

        // Act & Assert
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void shouldUpdateOrder() throws Exception {
        // Arrange
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, 1L, 5, BigDecimal.valueOf(100));
        OrderDto orderDto = new OrderDto(1L, 1L, BigDecimal.valueOf(600), null, LocalDateTime.now(), LocalDateTime.now(), List.of(itemDto));
        OrderDto updatedOrder = new OrderDto(1L, 1L, BigDecimal.valueOf(600), null, LocalDateTime.now(), LocalDateTime.now(), List.of(itemDto));
        when(orderService.updateOrder(1L, orderDto)).thenReturn(updatedOrder);

        // Act & Assert
        mockMvc.perform(put("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(600))
                .andExpect(jsonPath("$.item.size()").value(1));
    }

    @Test
    void shouldReturnNotFoundWhenUpdateOrderNotExist() throws Exception {
        // Arrange
        OrderItemDto itemDto = new OrderItemDto(1L, 1L, 1L, 5, BigDecimal.valueOf(100));
        OrderDto orderDto = new OrderDto(999L, 1L, BigDecimal.valueOf(700), null, LocalDateTime.now(), LocalDateTime.now(), List.of(itemDto));
        when(orderService.updateOrder(999L, orderDto)).thenThrow(new ResourceNotFoundException("Order not found"));

        // Act & Assert
        mockMvc.perform(put("/orders/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteOrder() throws Exception {
        // Arrange
        when(orderService.deleteOrder(1L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/orders/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeleteOrderNotExist() throws Exception {
        // Arrange
        when(orderService.deleteOrder(999L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/orders/999"))
                .andExpect(status().isNotFound());
    }
}
