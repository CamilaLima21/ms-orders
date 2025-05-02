package br.com.fiap.msorders.domain.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderNotFoundExceptionTest {

    @Test
    void shouldCreateOrderNotFoundExceptionWithMessage() {
        // Verifica se a exceção é criada corretamente com a mensagem
        String message = "Order not found";
        OrderNotFoundException exception = new OrderNotFoundException(message);
        
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldExtendRuntimeException() {
        // Verifica se a exceção é uma subclasse de RuntimeException
        OrderNotFoundException exception = new OrderNotFoundException("Test message");
        
        assertTrue(exception instanceof RuntimeException);
    }
}
