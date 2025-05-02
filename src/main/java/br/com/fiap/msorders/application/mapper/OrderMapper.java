package br.com.fiap.msorders.application.mapper;

import br.com.fiap.msorders.application.dto.OrderDto;
import br.com.fiap.msorders.domain.model.Order;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderMapper(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    public Order toDomain(OrderDto dto) {
        return new Order(
            dto.id(),
            dto.clientId(),
            dto.total(),
            dto.status(),
            dto.createdAt(),
            dto.updatedAt(),
            dto.item() != null
                ? dto.item().stream().map(orderItemMapper::toDomain).collect(Collectors.toList())
                : new ArrayList<>()
        );
    }

    public Order toDomain(OrderEntity entity) {
        return new Order(
            entity.getId(),
            entity.getClientId(),
            entity.getTotal(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getOrderItems() != null
                ? entity.getOrderItems().stream().map(orderItemMapper::toDomain).collect(Collectors.toList())
                : new ArrayList<>()
        );
    }

    public OrderDto toDto(Order domain) {
        return new OrderDto(
            domain.getId(),
            domain.getClientId(),
            domain.getTotal(),
            domain.getStatus(),
            domain.getCreatedAt(),
            domain.getUpdatedAt(),
            domain.getItems() != null
                ? domain.getItems().stream().map(orderItemMapper::toDto).collect(Collectors.toList())
                : new ArrayList<>()
        );
    }

    public OrderEntity toEntity(Order domain) {
        OrderEntity entity = new OrderEntity();
        entity.setId(domain.getId());
        entity.setClientId(domain.getClientId());
        entity.setTotal(domain.getTotal());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getItems() != null && !domain.getItems().isEmpty()) {
            entity.setOrderItems(
                domain.getItems().stream()
                    .map(item -> orderItemMapper.toEntity(item, entity))
                    .collect(Collectors.toList())
            );
        } else {
            entity.setOrderItems(new ArrayList<>());
        }

        return entity;
    }
}
