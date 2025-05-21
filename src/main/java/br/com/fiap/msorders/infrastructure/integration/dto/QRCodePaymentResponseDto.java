package br.com.fiap.msorders.infrastructure.integration.dto;

import java.util.Map;

public record QRCodePaymentResponseDto(
    String paymentId,
    String status,
    String description,
    Map<String, Object> additionalData
) {}