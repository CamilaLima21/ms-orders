package br.com.fiap.msorders.infrastructure.integration.service;

import br.com.fiap.msorders.domain.exception.ClientNotFoundException;
import br.com.fiap.msorders.infrastructure.integration.client.ClientClient;
import feign.FeignException;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceClient {

    private final ClientClient clientClient;

    public ClientServiceClient(ClientClient clientClient) {
        this.clientClient = clientClient;
    }

    public void validateClientExists(Long clientId) {
        try {
            clientClient.validateClient(clientId);
        } catch (FeignException.NotFound e) {
            throw new ClientNotFoundException(clientId);
        }
    }
}
