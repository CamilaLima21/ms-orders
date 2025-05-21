package br.com.fiap.msorders.infrastructure.integration.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import br.com.fiap.msorders.infrastructure.integration.dto.StockDto;

@FeignClient(name = "ms-stock", url = "${ms.stock.url}")
public interface StockClient {
	@GetMapping("/stocks/sku/{sku}")
	ResponseEntity<StockDto> searchStock(@PathVariable String sku);
	
	@PostMapping("/stocks/decrease")
	ResponseEntity<Void> decreaseStock(@RequestParam("sku") String sku, @RequestParam("quantity") int quantity);
	
	@PostMapping("/stocks/increase")
	ResponseEntity<Void> increaseStock(@RequestParam("sku") String sku, @RequestParam("quantity") int quantity);
}