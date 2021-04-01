package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.model.dto.OrderDto;
import eu.accesa.onlinestore.model.dto.OrderDtoNoId;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;
import eu.accesa.onlinestore.repository.OrderRepository;
import eu.accesa.onlinestore.repository.ProductRepository;
import eu.accesa.onlinestore.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static eu.accesa.onlinestore.utils.OrderTestUtils.*;
import static eu.accesa.onlinestore.utils.ProductTestUtils.createProductEnity;
import static eu.accesa.onlinestore.utils.UserTestUtils.createUserEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Spy
    private ModelMapper mapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private EmailServiceImpl emailService;

    @Test
    public void findAll() {
        when(orderRepository.findAll()).thenReturn(testOrdersList());
        final List<OrderDto> foundOrders = orderService.findAll();
        assertNotNull(foundOrders, "List is empty");
        assertEquals(foundOrders.size(), 2, "List size doesn't match actual size");
        verify(orderRepository).findAll();
    }

    @Test
    public void findById() {
        String id = "60377ec00e2cb07c9a3811d3";
        OrderEntity foundOrder = testOrderEntity("60377ec00e2cb07c9a3811d3", 11.11, createUserEntity("603648273ed85832b440eb99",
                "John", "Doe", "jd@mockemail.com", "jd", "qwerty",
                "40722112211", "male", "Toamnei nr.1", "Las Vegas", "Nevada", "440055"));
        when(orderRepository.findById(id)).thenReturn(Optional.of(foundOrder));
        OrderDto foundOrderDto = orderService.findById(id);
        assertEquals(foundOrderDto.getId(), id, "ID mismatch");
        verify(orderRepository).findById(id);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    @Disabled
    public void createOrder() {

        //given
        LocalDateTime orderDate = LocalDateTime.now();
        String userId = "603648273ed85832b440eb99";
        UserEntity user = createUserEntity("603648273ed85832b440eb99",
                "John", "Doe", "jd@mockemail.com", "jd", "qwerty",
                "40722112211", "male", "Toamnei nr.1", "Las Vegas", "Nevada", "440055");
        OrderDtoNoId order = testOrderDtoNoId(100.1, "603648273ed85832b440eb99");
        HashMap<String, Integer> orderedProducts = testHMOrderedProduct("6040d6ba1e240556a8b76e8a1", 1);
        order.setOrderedProducts(orderedProducts);
        order.setOrderDate(orderDate);
        OrderEntity orderEntity = testOrderEntity(null, 100.1, user);
        orderEntity.setOrderDate(orderDate);
        OrderEntity savedOrder = testOrderEntity("savedOrderId", 100.1, user);
        savedOrder.setOrderDate(orderDate);
        ProductEntity productEntity1 = createProductEnity("6040d6ba1e240556a8b76e8a1", null, null, null, null, null, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);
        when(productRepository.findById(anyString())).thenReturn(Optional.of(productEntity1));

        OrderDto createdOrderDto = orderService.createOrder(order);
        assertNotNull(createdOrderDto, "Created Order can't be null");
        assertNotNull(createdOrderDto.getId(), "ID can't be null");

        assertEquals(createdOrderDto.getOrderDate(), order.getOrderDate());
        assertEquals(createdOrderDto.getOrderValue(), 100.1);
        assertEquals(createdOrderDto.getOrderedProducts().size(), 3);
        verify(orderRepository).save(any(OrderEntity.class));
        verify(userRepository).findById(userId);
    }

    @Test
    public void updateOrder() {

        String orderId = "savedOrderId";
        OrderEntity orderFound = testOrderEntity("savedOrderId", 100.1, null);
        OrderEntity savedOrder = testOrderEntity("updatedOrderId", 1.1, null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderFound));
        when(orderRepository.save(orderFound)).thenReturn(savedOrder);

        OrderDto orderDto = testOrderDto(savedOrder.getId(), savedOrder.getOrderValue(), null);
        OrderDto updatedOrderDto = orderService.updateOrder(orderId, orderDto);
        assertNotNull(updatedOrderDto);
        assertEquals(updatedOrderDto.getId(), "updatedOrderId", "ID mismatch !!");
        assertEquals(updatedOrderDto.getOrderValue(), 1.1, "field value doesn't match");
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(orderFound);
        verifyNoMoreInteractions(orderRepository);
    }

}
