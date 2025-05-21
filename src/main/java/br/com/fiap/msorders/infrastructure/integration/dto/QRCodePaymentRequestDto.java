package br.com.fiap.msorders.infrastructure.integration.dto;

public record QRCodePaymentRequestDto(
    double amount,
    String currency,
    String orderId,
    String customerId
) {}