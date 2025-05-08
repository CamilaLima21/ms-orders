package br.com.fiap.msorders.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.domain.model.OrderItem;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderEntity;
import br.com.fiap.msorders.infrastructure.persistence.entity.OrderItemEntity;

@Component
public class OrderItemMapper {

    public OrderItemEntity toEntity(OrderItem domain, OrderEntity order) {
        if (domain == null || order == null) return null;

        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(domain.getId());
        entity.setProductSku(domain.getProductSku());
        entity.setQuantity(domain.getQuantity());
        entity.setPrice(domain.getPrice());
        entity.setOrder(order);
        return entity;
    }

    public OrderItem toDomain(OrderItemEntity entity) {
        if (entity == null) return null;

        return new OrderItem(
            entity.getId(),
            entity.getOrder() != null ? entity.getOrder().getId() : 0,
            entity.getProductSku(),
            entity.getQuantity(),
            entity.getPrice()
        );
    }

    public OrderItemDto toDto(OrderItem domain) {
        if (domain == null) return null;

        return new OrderItemDto(
            domain.getId(),
            domain.getOrderId(),
            domain.getProductSku(),
            domain.getQuantity(),
            domain.getPrice()
        );
    }

    
    public OrderItemDto toDto(OrderItemEntity entity) {
        if (entity == null) return null;
        
        return toDto(toDomain(entity)); 
    }

    public List<OrderItemDto> toDtoList(List<OrderItemEntity> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public OrderItem toDomain(OrderItemDto dto) {
        if (dto == null) return null;

        return new OrderItem(
            dto.id(),
            dto.orderId(),
            dto.productSku(),
            dto.quantity(),
            dto.price()
        );
    }

    public OrderItemEntity toEntity(OrderItemDto dto, OrderEntity order) {
        if (dto == null || order == null) return null;

        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(dto.id());
        entity.setProductSku(dto.productSku());
        entity.setQuantity(dto.quantity());
        entity.setPrice(dto.price());
        entity.setOrder(order);
        return entity;
    }

    public void updateFromDto(OrderItemDto dto, OrderItemEntity entity, OrderEntity order) {
        if (dto == null || entity == null || order == null) return;

        entity.setProductSku(dto.productSku());
        entity.setQuantity(dto.quantity());
        entity.setPrice(dto.price());
        entity.setOrder(order);
    }
}
