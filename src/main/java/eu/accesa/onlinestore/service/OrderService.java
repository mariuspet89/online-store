package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.OrderDto;
import eu.accesa.onlinestore.model.dto.OrderDtoWithoutId;
import org.springframework.data.domain.Sort.Order;

import java.util.List;

public interface OrderService {

    OrderDtoWithoutId createOrder(OrderDtoWithoutId orderDtoWithoutId);
    OrderDtoWithoutId updateOrder(OrderDto orderDto);
    List<OrderDto> getOrdersByUser(String userId);
    OrderDto getOrderById(String orderId);
    List<OrderDto>getAllOrders();


}
