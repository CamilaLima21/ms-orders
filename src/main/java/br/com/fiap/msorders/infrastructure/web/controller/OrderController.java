package br.com.fiap.msorders.infrastructure.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.fiap.msorders.application.dto.OrderDto;
import br.com.fiap.msorders.application.service.OrderService;
import br.com.fiap.msorders.infrastructure.web.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderDto orderDto) {
        OrderDto createdOrder = orderService.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }


    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable long id) {
        try {
            OrderDto order = orderService.findOrderById(id);
            return ResponseEntity.ok(order);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.findAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable long id, @RequestBody OrderDto orderDto) {
        try {
            OrderDto updatedOrder = orderService.updateOrder(id, orderDto);
            return ResponseEntity.ok(updatedOrder);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable long id) {
        boolean isDeleted = orderService.deleteOrder(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build();  // 204 No Content
        } else {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }
    }
    
    @PostMapping("/{id}/payment")
    public ResponseEntity<String> processPayment(
            @PathVariable long id,
            @RequestParam String paymentMethod) {
        try {
            if (!paymentMethod.equalsIgnoreCase("PIX") && !paymentMethod.equalsIgnoreCase("CARD")) {
                return ResponseEntity.badRequest().body("Invalid payment method. Use 'PIX' or 'CARD'.");
            }
            orderService.processPayment(id, paymentMethod);
            return ResponseEntity.ok("Payment processed successfully.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment processing failed.");
        }
    }
}
