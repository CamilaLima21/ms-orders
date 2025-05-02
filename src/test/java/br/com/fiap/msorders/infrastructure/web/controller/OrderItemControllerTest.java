package br.com.fiap.msorders.infrastructure.web.controller;

import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.application.service.OrderItemService;
import br.com.fiap.msorders.infrastructure.web.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest  // Anotação para carregar o contexto do Spring
class OrderItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderItemController orderItemController;

    @Autowired  // Necessário para injetar o MockMvc corretamente no Spring
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Configuração do MockMvc com o controlador
        mockMvc = MockMvcBuilders.standaloneSetup(orderItemController)
                                 .build();  // Construir MockMvc corretamente
    }

    @Test
    void shouldCreateOrderItem() throws Exception {
        // Dados de entrada
        OrderItemDto dto = new OrderItemDto(1L, 1L, 1L, 2, BigDecimal.valueOf(100.00));
        
        // Mock do serviço
        when(orderItemService.create(dto)).thenReturn(dto);

        // Perform da requisição POST
        mockMvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.price").value(100.00))
                .andExpect(jsonPath("$.quantity").value(2));

        verify(orderItemService, times(1)).create(dto);
    }

    @Test
    void shouldReturnBadRequestWhenCreateOrderItemWithInvalidData() throws Exception {
        // Dados inválidos (quantidade zero)
        OrderItemDto dto = new OrderItemDto(1L, 1L, 1L, 0, BigDecimal.valueOf(100.00));

        // Perform da requisição POST
        mockMvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());  // Esperando um erro 400

        verify(orderItemService, times(0)).create(dto);  // Garantir que o serviço não foi chamado
    }


    @Test
    void shouldGetAllOrderItems() throws Exception {
        OrderItemDto dto = new OrderItemDto(1L, 1L, 1L, 2, BigDecimal.valueOf(100.00));
        when(orderItemService.findAll()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/order-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1L))
                .andExpect(jsonPath("$[0].productId").value(1L));

        verify(orderItemService, times(1)).findAll();
    }

    @Test
    void shouldGetOrderItemById() throws Exception {
        OrderItemDto dto = new OrderItemDto(1L, 1L, 1L, 2, BigDecimal.valueOf(100.00));
        when(orderItemService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/order-items/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.productId").value(1L));

        verify(orderItemService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenGetOrderItemByIdNotExist() throws Exception {
        when(orderItemService.findById(1L)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/order-items/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(orderItemService, times(1)).findById(1L);
    }

    @Test
    void shouldUpdateOrderItem() throws Exception {
        OrderItemDto dto = new OrderItemDto(1L, 1L, 1L, 3, BigDecimal.valueOf(150.00));
        when(orderItemService.update(1L, dto)).thenReturn(dto);

        mockMvc.perform(put("/order-items/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(150.00))
                .andExpect(jsonPath("$.quantity").value(3));

        verify(orderItemService, times(1)).update(1L, dto);
    }

    @Test
    void shouldDeleteOrderItem() throws Exception {
        when(orderItemService.delete(1L)).thenReturn("Order item deleted successfully!");

        mockMvc.perform(delete("/order-items/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Order item deleted successfully!"));

        verify(orderItemService, times(1)).delete(1L);
    }

    @Test
    void shouldReturnNotFoundWhenDeleteOrderItemNotExist() throws Exception {
        when(orderItemService.delete(1L)).thenThrow(ResourceNotFoundException.class);

        mockMvc.perform(delete("/order-items/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(orderItemService, times(1)).delete(1L);
    }
}
