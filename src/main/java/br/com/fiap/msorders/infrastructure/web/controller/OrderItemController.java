package br.com.fiap.msorders.infrastructure.web.controller;

import br.com.fiap.msorders.application.dto.OrderItemDto;
import br.com.fiap.msorders.application.service.OrderItemService;
import br.com.fiap.msorders.infrastructure.web.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService service;

    public OrderItemController(OrderItemService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<OrderItemDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemDto> findById(@PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderItemDto> create(@Valid @RequestBody OrderItemDto dto) throws ResourceNotFoundException {
        // Verificando se a quantidade é válida
        if (dto.quantity() == null || dto.quantity() <= 0) {
            return ResponseEntity.badRequest().build();  // Retornando 400 se a quantidade for inválida
        }

        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDto> update(@PathVariable long id, @Valid @RequestBody OrderItemDto dto) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(service.delete(id));
    }
}