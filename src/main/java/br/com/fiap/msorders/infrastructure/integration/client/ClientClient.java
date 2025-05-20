package br.com.fiap.msorders.infrastructure.integration.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-clients", url = "${ms.clients.url}")
public interface ClientClient {
	@GetMapping("/clients/{id}")
	ResponseEntity<Void> validateClient(@PathVariable Long id);

}