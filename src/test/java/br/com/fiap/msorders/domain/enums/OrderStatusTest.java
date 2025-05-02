package br.com.fiap.msorders.domain.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    void shouldReturnCorrectEnumFromString() {
        // Testa a conversão de string para enum
        assertEquals(OrderStatus.CREATED, OrderStatus.valueOf("CREATED"));
        assertEquals(OrderStatus.PAID, OrderStatus.valueOf("PAID"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForInvalidString() {
        // Testa se gera exceção ao passar uma string inválida
        assertThrows(IllegalArgumentException.class, () -> OrderStatus.valueOf("INVALID_STATUS"));
    }

    @Test
    void shouldGetCorrectEnumValues() {
        // Testa se os valores do enum estão corretos
        assertEquals(3, OrderStatus.values().length);
        assertTrue(OrderStatus.values()[0] == OrderStatus.CREATED);
        assertTrue(OrderStatus.values()[1] == OrderStatus.PAID);
        assertTrue(OrderStatus.values()[2] == OrderStatus.CANCELLED);
    }

    @Test
    void shouldCompareEnumValuesCorrectly() {
        // Testa se a comparação entre valores do enum funciona corretamente
        assertTrue(OrderStatus.CREATED == OrderStatus.CREATED);
        assertFalse(OrderStatus.PAID == OrderStatus.CANCELLED);
        assertFalse(OrderStatus.CREATED == OrderStatus.PAID);
    }
}
