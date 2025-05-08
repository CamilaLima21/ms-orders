package br.com.fiap.msorders.application.dto;

import java.math.BigDecimal;

public record ProductDto(
	    long id,
	    String name,
	    String productSku,
	    BigDecimal price
	) {}
