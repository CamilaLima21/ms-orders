package br.com.fiap.msorders.infrastructure.web.exceptions;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorDetailsTest {

    @Test
    void shouldCreateErrorDetailsWithValidValues() {
        // Arrange
        Date timestamp = new Date();
        String message = "Some error occurred";
        String details = "Error details";

        // Act
        ErrorDetails errorDetails = new ErrorDetails(timestamp, message, details);

        // Assert
        assertNotNull(errorDetails);
        assertEquals(timestamp, errorDetails.getTimestamp());
        assertEquals(message, errorDetails.getMessage());
        assertEquals(details, errorDetails.getDetails());
    }

    @Test
    void shouldCreateErrorDetailsWithCurrentTimestamp() {
        // Arrange
        Date timestamp = new Date();
        String message = "Some error occurred";
        String details = "Error details";

        // Act
        ErrorDetails errorDetails = new ErrorDetails(timestamp, message, details);

        // Assert
        assertNotNull(errorDetails.getTimestamp());
        assertEquals(message, errorDetails.getMessage());
        assertEquals(details, errorDetails.getDetails());
    }
}
