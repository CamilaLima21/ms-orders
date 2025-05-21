package br.com.fiap.msorders.infrastructure.integration.dto;

public record CreditCardPaymentResponseDto(
    String paymentId,
    String status,
    String description,
    String authorizationCode,
    double amount,
    String currency,
    String orderId
) {}