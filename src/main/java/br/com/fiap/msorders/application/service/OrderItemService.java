package br.com.fiap.msorders.application.service;

import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.application.mapper.OrderItemMapper;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderItemEntity;
import br.com.fiap.msorders.infrastructure.persistence.repository.OrderItemRepository;
import br.com.fiap.msorders.infrastructure.persistence.repository.OrderRepository;
import br.com.fiap.msorders.infrastructure.web.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemMapper mapper;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository, OrderItemMapper mapper) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    public OrderItemDto create(OrderItemDto dto) throws ResourceNotFoundException {
        if (dto.quantity() == null || dto.quantity() <= 0)
            throw new IllegalArgumentException("Invalid quantity");

        OrderEntity order = orderRepository.findById(dto.orderId())
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        OrderItemEntity saved = orderItemRepository.save(mapper.toEntity(dto, order));
        return mapper.toDto(saved);
    }

    public List<OrderItemDto> findAll() {
        return mapper.toDtoList(orderItemRepository.findAll());
    }

    public OrderItemDto findById(long id) throws ResourceNotFoundException {
        return mapper.toDto(
            orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"))
        );
    }

    public OrderItemDto update(long id, OrderItemDto dto) throws ResourceNotFoundException {
        OrderItemEntity entity = orderItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        OrderEntity order = orderRepository.findById(dto.orderId())
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        mapper.updateFromDto(dto, entity, order);
        return mapper.toDto(orderItemRepository.save(entity));
    }

    public String delete(long id) throws ResourceNotFoundException {
        OrderItemEntity entity = orderItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
        orderItemRepository.delete(entity);
        return "Order item deleted successfully!";
    }
}