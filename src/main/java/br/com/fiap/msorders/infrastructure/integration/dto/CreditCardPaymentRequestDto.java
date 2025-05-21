package br.com.fiap.msorders.infrastructure.integration.dto;

import java.util.List;

public record CreditCardPaymentRequestDto(
    String seller_id,
    double amount,
    String currency,
    Order order,
    Customer customer,
    Credit credit
) {
    public static record Order(
        String order_id,
        List<Item> items
    ) {
        public static record Item(
            String name,
            int quantity,
            double unit_amount
        ) {}
    }

    public static record Customer(
        String customer_id,
        String first_name,
        String last_name,
        String email,
        String phone
    ) {}

    public static record Credit(
        Card card,
        int installments
    ) {
        public static record Card(
            String number_token,
            String cardholder_name,
            String security_code,
            String expiration_month,
            String expiration_year
        ) {}
    }
}