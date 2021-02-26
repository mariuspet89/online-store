package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.OnlineStoreApplication;
import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
import eu.accesa.onlinestore.model.dto.OrderDto;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;
import eu.accesa.onlinestore.repository.OrderRepository;
import eu.accesa.onlinestore.repository.ProductRepository;
import eu.accesa.onlinestore.repository.UserRepository;
import eu.accesa.onlinestore.service.OrderService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnlineStoreApplication.class);
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper mapper;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository, ModelMapper mapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        LOGGER.info("Service: creating order with id: {}", orderDto.getId());

        UserEntity userEntity = userRepository.findById(orderDto.getUserId()).
                orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getName(),
                        " UserID ", orderDto.getUserId()));
        OrderEntity newOrder = mapper.map(orderDto, OrderEntity.class);
        newOrder.setUser(userEntity);

        for (String productId : orderDto.getOrderedProducts().keySet()) {
            if (productRepository.findById(productId).isEmpty()) {
                throw new EntityNotFoundException(ProductEntity.class.getName(), "ProductId", productId);
            }
        }
        return mapper.map(orderRepository.save(newOrder), OrderDto.class);

    }

    @Override
    public OrderDto updateOrder(OrderDto orderDto) {
        LOGGER.info("Service: updating order with old values: {} with new values {}",
                orderRepository.findById(orderDto.getId()).toString(), orderDto.toString());
        OrderEntity oldOrder = orderRepository.findById(orderDto.getId()).orElseThrow(() ->
                new EntityNotFoundException(OrderEntity.class.getName(), "OrderId", orderDto.getId()));
        mapper.map(orderDto, oldOrder);
        return mapper.map(orderRepository.save(oldOrder), OrderDto.class);
    }

    @Override
    public List<OrderDto> getOrdersByUser(String userId) {
        LOGGER.info("Service: searching for order of user with id : {}", userId);
        List<OrderEntity> orders = orderRepository.getOrderEntitiesByUserId(userId);
        return orders.stream().map(orderEntity -> mapper.map(orderEntity, OrderDto.class)).collect(toList());
    }

    @Override
    public OrderDto getOrderById(String orderId) {
        LOGGER.info("Service: searching for order with id : {}", orderId);
        return mapper.map(orderRepository.findById(orderId).orElseThrow(() ->
                new EntityNotFoundException(OrderEntity.class.getName(), "OrderID", orderId)), OrderDto.class);
    }

    @Override
    public List<OrderDto> getAllOrders() {
        LOGGER.info("Service: getting  all  order");
        List<OrderEntity> orders=orderRepository.findAll();
        return orders.stream().map(orderEntity -> mapper.map(orderEntity,OrderDto.class)).collect(toList());
    }

}