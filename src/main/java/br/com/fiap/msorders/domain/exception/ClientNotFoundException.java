package br.com.fiap.msorders.domain.exception;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long clientId) {
        super("Client with ID " + clientId + " not found.");
    }

    public ClientNotFoundException(String message) {
        super(message);
    }
}
