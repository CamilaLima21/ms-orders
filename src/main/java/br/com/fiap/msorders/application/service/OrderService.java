package br.com.fiap.msorders.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.fiap.msorders.application.dto.OrderDto;
import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.infrastructure.integration.dto.CreditCardPaymentRequestDto;
import br.com.fiap.msorders.infrastructure.integration.dto.CreditCardPaymentResponseDto;
import br.com.fiap.msorders.infrastructure.integration.dto.QRCodePaymentRequestDto;
import br.com.fiap.msorders.infrastructure.integration.dto.QRCodePaymentResponseDto;
import br.com.fiap.msorders.infrastructure.integration.dto.StatusDto;
import br.com.fiap.msorders.infrastructure.integration.dto.StockDto;
import br.com.fiap.msorders.infrastructure.integration.dto.TokenResponseDto;
import br.com.fiap.msorders.application.mapper.OrderMapper;
import br.com.fiap.msorders.domain.enums.OrderStatus;
import br.com.fiap.msorders.infrastructure.integration.service.ClientServiceClient;
import br.com.fiap.msorders.infrastructure.integration.service.PaymentServiceClient;
import br.com.fiap.msorders.infrastructure.integration.service.ProductServiceClient;
import br.com.fiap.msorders.infrastructure.integration.service.StockServiceClient;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderItemEntity;
import br.com.fiap.msorders.infrastructure.persistence.repository.OrderRepository;
import br.com.fiap.msorders.infrastructure.web.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ClientServiceClient clientServiceClient;
    private final ProductServiceClient productServiceClient;
    private final StockServiceClient stockServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    @Value("${payment.seller-id}")
    private String sellerId;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper,
    		ClientServiceClient clientServiceClient, ProductServiceClient productServiceClient, StockServiceClient stockServiceClient, PaymentServiceClient paymentServiceClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.clientServiceClient = clientServiceClient;
        this.productServiceClient = productServiceClient;
        this.stockServiceClient = stockServiceClient;  
        this.paymentServiceClient = paymentServiceClient;
    }

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        if (orderDto.clientId() == 0) {
            throw new IllegalArgumentException("Client ID must be provided");
        }

        clientServiceClient.validateClientExists(orderDto.clientId());

        if (orderDto.items() == null || orderDto.items().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        
        List<String> skus = orderDto.items()
                .stream()
                .map(OrderItemDto::productSku)
                .toList();

        productServiceClient.validateSkus(skus);
                
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStatus(OrderStatus.CREATED);
        
        // Check stock for each item
        for (OrderItemDto item : orderDto.items()) {
            StockDto stock = stockServiceClient.searchStock(item.productSku());
            if (stock.quantity() < item.quantity()) {
            	logger.error("Insufficient stock for product SKU: {}. Requested: {}, Available: {}", 
            		    item.productSku(), item.quantity(), stock.quantity());
            	 orderEntity.setStatus(OrderStatus.FAILED_NOT_STOCK);
            }
        }
        
        // Decrease stock for each item
        for (OrderItemDto item : orderDto.items()) {
            stockServiceClient.decreaseStock(item.productSku(), item.quantity());
        }
        orderEntity.setClientId(orderDto.clientId());
        orderEntity.setTotal(calculateTotal(orderDto.items()));
        orderEntity.setCreatedAt(LocalDateTime.now());
        orderEntity.setUpdatedAt(LocalDateTime.now());

        for (OrderItemDto itemDto : orderDto.items()) {
            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setProductSku(itemDto.productSku());
            itemEntity.setQuantity(itemDto.quantity());
            itemEntity.setPrice(itemDto.price());
            orderEntity.addOrderItem(itemEntity);
        }
		orderEntity.getOrderItems().forEach(item -> item.setOrder(orderEntity));
        OrderEntity saved = orderRepository.save(orderEntity);
        
        return orderMapper.toDto(orderMapper.toDomain(saved));
    }

    @Transactional
	public OrderDto processPayment(long id, String paymentMethod) throws ResourceNotFoundException, InterruptedException {
		OrderEntity orderEntity = orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));

		TokenResponseDto tokenResponse = paymentServiceClient.generateToken("client_credentials", String.valueOf(orderEntity.getClientId()), "client_secret", "oob");
		boolean paymentSuccessful = false;
		
		switch (paymentMethod) {
	    case "CARD":

	        CreditCardPaymentRequestDto creditCardRequest = new CreditCardPaymentRequestDto(
	        	sellerId,
	            orderEntity.getTotal().doubleValue(),
	            "BRL",
	            new CreditCardPaymentRequestDto.Order(
	                String.valueOf(orderEntity.getId()),
	                orderEntity.getOrderItems().stream()
	                    .map(item -> new CreditCardPaymentRequestDto.Order.Item(
	                        item.getProductSku(),
	                        item.getQuantity(),
	                        item.getPrice().doubleValue()
	                    ))
	                    .toList()
	            ),
	            new CreditCardPaymentRequestDto.Customer(
	                String.valueOf(orderEntity.getClientId()),
	                "","","",""
	            ),
	            new CreditCardPaymentRequestDto.Credit(
	                new CreditCardPaymentRequestDto.Credit.Card(
	                    "","","","",""
	                ),
	                1
	            )
	        );
	        CreditCardPaymentResponseDto creditCardResponse = paymentServiceClient.processCreditCardPayment(creditCardRequest, "Bearer " + tokenResponse.access_token());
	        logger.info("Card Payment Response: {}", creditCardResponse);
	        if (creditCardResponse.status() != null && creditCardResponse.status().equalsIgnoreCase("APPROVED")) {
                logger.info("Payment confirmed for Order ID: {}", orderEntity.getId());
                paymentSuccessful = true;
            } else {
                logger.warn("Payment not confirmed for Order ID: {}", orderEntity.getId());
                paymentSuccessful = false;
            }
	        break;
	        
	    case "PIX":
	    	
	        QRCodePaymentRequestDto qrCodeRequest = new QRCodePaymentRequestDto(
	            orderEntity.getTotal().doubleValue(),
	            "BRL",
	            String.valueOf(orderEntity.getId()),
	            String.valueOf(orderEntity.getClientId())
	        );
	        
	        QRCodePaymentResponseDto qrCodeResponse = paymentServiceClient.generateQRCodePayment(qrCodeRequest, "Bearer " + tokenResponse.access_token());
	        logger.info("QR Code Payment Response: {}", qrCodeResponse);
	        
	        for (int i = 0; i < 6; i++) {
	            Thread.sleep(5000);

	            ResponseEntity<StatusDto> statusResponse = paymentServiceClient.getStatus(
	                String.valueOf(orderEntity.getId()), 
	                "Bearer " + tokenResponse.access_token()
	            );

	            StatusDto status = statusResponse.getBody();
	            logger.info("Payment Status: {}", status);

	            if (status != null && "APPROVED".equalsIgnoreCase(status.status())) {
	                logger.info("Payment confirmed for Order ID: {}", orderEntity.getId());
	                break;
	            }

	            if (i == 5) {
	                logger.warn("Payment not confirmed for Order ID: {}", orderEntity.getId());
	                
	                orderEntity.setStatus(OrderStatus.FAILED_NOT_PAID);
	            }
	        }
	        
	        break;
	        
	    default:
	        throw new IllegalArgumentException("Invalid payment method");
	}

		// Payment Update
		if (paymentSuccessful) {
			orderEntity.setStatus(OrderStatus.CLOSED_SUCCESS);
		} else {
			orderEntity.setStatus(OrderStatus.FAILED_NOT_PAID);
		}
		orderEntity.setUpdatedAt(LocalDateTime.now());

		OrderEntity saved = orderRepository.save(orderEntity);
		return orderMapper.toDto(orderMapper.toDomain(saved));
	}

    public OrderDto findOrderById(long id) throws ResourceNotFoundException {
        OrderEntity orderEntity = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return orderMapper.toDto(orderMapper.toDomain(orderEntity));
    }

    public List<OrderDto> findAllOrders() {
        return orderRepository.findAll().stream()
            .map(orderMapper::toDomain)
            .map(orderMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto updateOrder(Long id, OrderDto orderDto) throws ResourceNotFoundException {
        OrderEntity existingOrder = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        existingOrder.setClientId(orderDto.clientId());
        existingOrder.setStatus(orderDto.status());
        existingOrder.setTotal(orderDto.total());
        existingOrder.setUpdatedAt(LocalDateTime.now());

        existingOrder.getOrderItems().clear();

        if (orderDto.items() != null) {
            List<OrderItemEntity> newItems = orderDto.items().stream()
                .map(dto -> {
                    OrderItemEntity item = new OrderItemEntity();
                    item.setProductSku(dto.productSku());
                    item.setQuantity(dto.quantity());
                    item.setPrice(dto.price());
                    item.setOrder(existingOrder);
                    return item;
                }).collect(Collectors.toList());
            existingOrder.getOrderItems().addAll(newItems);
        }

        OrderEntity saved = orderRepository.save(existingOrder);
        return orderMapper.toDto(orderMapper.toDomain(saved));
    }

    @Transactional
    public boolean deleteOrder(long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private BigDecimal calculateTotal(List<OrderItemDto> items) {
        return items.stream()
            .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
}
