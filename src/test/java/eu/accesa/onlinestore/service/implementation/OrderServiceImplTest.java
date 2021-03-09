package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.model.dto.OrderDto;
import eu.accesa.onlinestore.model.entity.OrderEntity;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static eu.accesa.onlinestore.utils.TestUtils.*;


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

    @Test
    public void findAll() {
        when(orderRepository.findAll()).thenReturn(testOrdersList());
        final List<OrderDto> foundOrders=orderService.findAll();
        assertNotNull(foundOrders,"List is empty");
        assertEquals(foundOrders.size(),2,"List size doesn't match actual size");
        verify(orderRepository).findAll();
    }
    @Test
    public void findById(){
        String id="60377ec00e2cb07c9a3811d3";
        OrderEntity foundOrder=  testOrderEntity("60377ec00e2cb07c9a3811d3",11.11,testUserEntity("603648273ed85832b440eb99",
                "John","Doe","jd@mockemail.com","jd","qwerty",
                "40722112211","male","Toamnei nr.1","Las Vegas","Nevada","440055"));
        when(orderRepository.findById(id)).thenReturn(Optional.of(foundOrder));
        OrderDto foundOrderDto=orderService.findById(id);
        assertEquals(foundOrderDto.getId(),id,"ID mismatch");
        verify(orderRepository).findById(id);
        verifyNoMoreInteractions(orderRepository);
    }
}