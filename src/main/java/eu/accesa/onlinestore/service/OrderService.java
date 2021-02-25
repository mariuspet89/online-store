package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.OrderDto;
import org.springframework.data.domain.Sort.Order;

import java.util.List;

public interface OrderService {

    OrderDto createOrder(OrderDto orderDto);
    OrderDto updateOrder(OrderDto orderDto);
    List<OrderDto> getOrdersByUser(String userId);
    OrderDto getOrderById(String orderId);
    List<OrderDto>getAllOrders();


}
