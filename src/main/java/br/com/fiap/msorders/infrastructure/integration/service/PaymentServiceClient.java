package br.com.fiap.msorders.infrastructure.integration.service;

import br.com.fiap.msorders.infrastructure.integration.client.PaymentClient;
import br.com.fiap.msorders.infrastructure.integration.dto.CreditCardPaymentRequestDto;
import br.com.fiap.msorders.infrastructure.integration.dto.CreditCardPaymentResponseDto;
import br.com.fiap.msorders.infrastructure.integration.dto.QRCodePaymentRequestDto;
import br.com.fiap.msorders.infrastructure.integration.dto.QRCodePaymentResponseDto;
import br.com.fiap.msorders.infrastructure.integration.dto.TokenRequestDto;
import br.com.fiap.msorders.infrastructure.integration.dto.TokenResponseDto;

import org.springframework.stereotype.Service;

@Service
public class PaymentServiceClient {

    private final PaymentClient paymentClient;

    public PaymentServiceClient(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public TokenResponseDto generateToken(String grantType, String clientId, String clientSecret, String scope) {
        TokenRequestDto tokenRequest = new TokenRequestDto(grantType, clientId, clientSecret, scope);
        return paymentClient.generateToken(tokenRequest);
    }
    
    public CreditCardPaymentResponseDto processCreditCardPayment(CreditCardPaymentRequestDto request, String token) {
        return paymentClient.processCreditCardPayment(request, token);
    }

    public QRCodePaymentResponseDto generateQRCodePayment(QRCodePaymentRequestDto request, String token) {
        return paymentClient.generateQRCodePayment(request, token);
    }
}