package br.com.fiap.msorders.application.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.com.fiap.msorders.application.dto.OrderDto;
import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.application.mapper.OrderMapper;
import br.com.fiap.msorders.domain.enums.OrderStatus;
import br.com.fiap.msorders.domain.model.Order;
import br.com.fiap.msorders.infrastructure.integration.service.ClientServiceClient;
import br.com.fiap.msorders.infrastructure.integration.service.ProductServiceClient;
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


    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper,
    		ClientServiceClient clientServiceClient, ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.clientServiceClient = clientServiceClient;
        this.productServiceClient = productServiceClient;
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
        orderEntity.setClientId(orderDto.clientId());
        orderEntity.setTotal(calculateTotal(orderDto.items()));
        orderEntity.setStatus(OrderStatus.CREATED);
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
