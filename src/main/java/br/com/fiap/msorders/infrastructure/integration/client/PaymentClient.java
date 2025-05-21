package br.com.fiap.msorders.infrastructure.integration.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.fiap.msorders.infrastructure.integration.dto.CreditCardPaymentRequestDto;
import br.com.fiap.msorders.infrastructure.integration.dto.CreditCardPaymentResponseDto;
import br.com.fiap.msorders.infrastructure.integration.dto.QRCodePaymentRequestDto;
import br.com.fiap.msorders.infrastructure.integration.dto.QRCodePaymentResponseDto;
import br.com.fiap.msorders.infrastructure.integration.dto.StatusDto;
import br.com.fiap.msorders.infrastructure.integration.dto.TokenRequestDto;
import br.com.fiap.msorders.infrastructure.integration.dto.TokenResponseDto;

@FeignClient(name = "ms-payments", url = "${ms.payments.url}")
public interface PaymentClient {

    @PostMapping("/payment/generateToken")
    TokenResponseDto generateToken(@RequestBody TokenRequestDto tokenRequest);
    
    @PostMapping("/payment/card")
    CreditCardPaymentResponseDto processCreditCardPayment(
        @RequestBody CreditCardPaymentRequestDto request,
        @RequestHeader("Authorization") String authorization
    );

    @PostMapping("/payment/generateQR")
    QRCodePaymentResponseDto generateQRCodePayment(
    		@RequestBody QRCodePaymentRequestDto request,
    		@RequestHeader("Authorization") String authorization
    );
    
    @GetMapping("payment/{paymentId}")
    ResponseEntity<StatusDto> getStatus(
        @PathVariable("paymentId") String paymentId,
        @RequestHeader("Authorization") String authorization
    );
}

