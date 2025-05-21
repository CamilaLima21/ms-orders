package br.com.fiap.msorders.infrastructure.integration.dto;

public record TokenResponseDto(
	    String access_token,
	    String token_type,
	    int expires_in,
	    String scope
	) {}