package br.com.fiap.msorders.infrastructure.integration.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.fiap.msorders.application.dto.ProductDto;

@FeignClient(name = "ms-products", url = "${ms.products.url}")
public interface ProductClient {
	@GetMapping("/products/sku")
    List<ProductDto> validateSkus(@RequestParam("skus") List<String> skus);
    
}



