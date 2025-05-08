package br.com.fiap.msorders.infrastructure.web.controller;

import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.application.service.OrderItemService;
import br.com.fiap.msorders.infrastructure.web.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderItemController.class)
class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderItemService orderItemService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateOrderItem() throws Exception {
        OrderItemDto dto = new OrderItemDto(1L, 1L, "SKU123", 2, BigDecimal.valueOf(100.00));
        when(orderItemService.create(dto)).thenReturn(dto);

        mockMvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.productSku").value("SKU123"))
                .andExpect(jsonPath("$.price").value(100.00))
                .andExpect(jsonPath("$.quantity").value(2));

        verify(orderItemService, times(1)).create(dto);
    }

    @Test
    void shouldReturnBadRequestWhenCreateOrderItemWithInvalidData() throws Exception {
        OrderItemDto dto = new OrderItemDto(1L, 1L, "SKU123", 0, BigDecimal.valueOf(100.00));

        mockMvc.perform(post("/order-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(orderItemService, never()).create(any());
    }

    @Test
    void shouldGetAllOrderItems() throws Exception {
        OrderItemDto dto = new OrderItemDto(1L, 1L, "SKU123", 2, BigDecimal.valueOf(100.00));
        when(orderItemService.findAll()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/order-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].productSku").value("SKU123"))  // Corrigido para verificar como String
                .andExpect(jsonPath("$[0].price").value(100.00))
                .andExpect(jsonPath("$[0].quantity").value(2));

        verify(orderItemService, times(1)).findAll();
    }


    @Test
    void shouldGetOrderItemById() throws Exception {
        OrderItemDto dto = new OrderItemDto(1L, 1L, "SKU123", 2, BigDecimal.valueOf(100.00));
        when(orderItemService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/order-items/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.productSku").value("SKU123"))  // Corrigido para verificar como String
                .andExpect(jsonPath("$.price").value(100.00))
                .andExpect(jsonPath("$.quantity").value(2));

        verify(orderItemService, times(1)).findById(1L);
    }


    @Test
    void shouldReturnNotFoundWhenGetOrderItemByIdNotExist() throws Exception {
        when(orderItemService.findById(1L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/order-items/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(orderItemService, times(1)).findById(1L);
    }

    @Test
    void shouldUpdateOrderItem() throws Exception {
        OrderItemDto dto = new OrderItemDto(1L, 1L, "SKU123", 3, BigDecimal.valueOf(150.00));
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
        when(orderItemService.delete(1L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(delete("/order-items/{id}", 1L))
                .andExpect(status().isNotFound());

        verify(orderItemService, times(1)).delete(1L);
    }
}
