package br.com.fiap.msorders.infrastructure.integration.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import br.com.fiap.msorders.application.dto.StockDto;

@FeignClient(name = "ms-stock", url = "http://localhost:8080")
public interface StockClient {
	@GetMapping("/stocks/sku/{sku}")
	ResponseEntity<StockDto> searchStock(@PathVariable String sku);
}