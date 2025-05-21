package br.com.fiap.msorders.infrastructure.integration.dto;

public record TokenRequestDto (
	    String grant_type,
	    String client_id,
	    String client_secret,
	    String scope
	) {}