package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
import eu.accesa.onlinestore.model.dto.OrderDto;
import eu.accesa.onlinestore.model.dto.OrderDtoNoId;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;
import eu.accesa.onlinestore.repository.OrderRepository;
import eu.accesa.onlinestore.repository.ProductRepository;
import eu.accesa.onlinestore.repository.UserRepository;
import eu.accesa.onlinestore.service.OrderService;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final ModelMapper mapper;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(ModelMapper mapper, OrderRepository orderRepository, ProductRepository productRepository,
                            UserRepository userRepository) {
        this.mapper = mapper;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<OrderDto> findAll() {
        LOGGER.info("Service: getting  all  order");
        List<OrderEntity> orders = orderRepository.findAll();
        return orders.stream().map(orderEntity -> mapper.map(orderEntity, OrderDto.class)).collect(toList());
    }

    @Override
    public OrderDto findById(String orderId) {
        LOGGER.info("Service: searching for order with id : {}", orderId);
        return mapper.map(orderRepository.findById(orderId).orElseThrow(() ->
                new EntityNotFoundException(OrderEntity.class.getName(), "OrderID", orderId)), OrderDto.class);
    }

    @Override
    public List<OrderDto> findByUser(String userId) {
        LOGGER.info("Service: searching for order of user with id : {}", userId);
        List<OrderEntity> orders = orderRepository.getOrderEntitiesByUserId(userId);
        return orders.stream().map(orderEntity -> mapper.map(orderEntity, OrderDto.class)).collect(toList());
    }

    @Override
    public OrderDto createOrder(OrderDtoNoId orderDtoNoId) {
        LOGGER.info("Service: creating order");

        UserEntity userEntity = userRepository.findById(orderDtoNoId.getUserId()).
                orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getName(),
                        " UserID ", orderDtoNoId.getUserId()));

        ObjectId objectId = new ObjectId();
        OrderEntity newOrder = mapper.map(orderDtoNoId, OrderEntity.class);
        newOrder.setId(objectId.toString());
        newOrder.setUser(userEntity);

        for (String productId : orderDtoNoId.getOrderedProducts().keySet()) {
            if (productRepository.findById(productId).isEmpty()) {
                throw new EntityNotFoundException(ProductEntity.class.getName(), "ProductId", productId);
            }
        }
        return mapper.map(orderRepository.save(newOrder), OrderDto.class);

    }

    @Override
    public OrderDto updateOrder(String id, OrderDtoNoId orderDtoNoId) {
        LOGGER.info("Updating order with id = {}", id);

        OrderEntity orderEntity = orderRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(OrderEntity.class.getName(), "OrderId", id));

        mapper.map(orderDtoNoId, orderEntity);
        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);
        return mapper.map(savedOrderEntity, OrderDto.class);
    }
}
