package eu.accesa.onlinestore.controller;

import eu.accesa.onlinestore.model.dto.CartDto;
import eu.accesa.onlinestore.model.dto.OrderDto;
import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private OrderDto createOrder(@RequestBody OrderDto orderDto) {
        return orderService.createOrder(orderDto);
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders());
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    private OrderDto getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    private List<OrderDto> getOrdersByUserId(@PathVariable String userId) {
        return orderService.getOrdersByUser(userId);
    }
    @PutMapping
    public ResponseEntity<OrderDto> updateOrder(@Valid @RequestBody OrderDto orderDto ) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.updateOrder(orderDto));
    }
}
