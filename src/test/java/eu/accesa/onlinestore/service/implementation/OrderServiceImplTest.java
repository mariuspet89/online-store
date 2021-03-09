package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.repository.OrderRepository;
import eu.accesa.onlinestore.repository.ProductRepository;
import eu.accesa.onlinestore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
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



}