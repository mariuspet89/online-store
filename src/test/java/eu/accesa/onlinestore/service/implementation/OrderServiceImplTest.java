package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.model.dto.OrderDto;
import eu.accesa.onlinestore.model.dto.OrderDtoNoId;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;
import eu.accesa.onlinestore.model.invoice.ProductLine;
import eu.accesa.onlinestore.repository.OrderRepository;
import eu.accesa.onlinestore.repository.ProductRepository;
import eu.accesa.onlinestore.repository.UserRepository;
import eu.accesa.onlinestore.service.InvoiceGeneratorService;
import eu.accesa.onlinestore.utils.ProductTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static eu.accesa.onlinestore.utils.OrderTestUtils.*;
import static eu.accesa.onlinestore.utils.ProductTestUtils.createProductEnity;
import static eu.accesa.onlinestore.utils.UserTestUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Spy
    private ModelMapper mapper;

    @Mock
    private MessageSource messageSource;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private InvoiceGeneratorService invoiceGeneratorService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Captor
    private ArgumentCaptor<OrderEntity> orderEntityCaptor;

    @Captor
    private ArgumentCaptor<List<ProductLine>> productLineListCaptor;

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
    void createOrder() {
        // GIVEN

        // prepare orderDtoNoId
        String userId = "603648273ed85832b440eb99";
        OrderDtoNoId orderDtoNoId = testOrderDtoNoId(5000.1, userId);

        LocalDateTime orderDate = LocalDateTime.now();
        orderDtoNoId.setOrderDate(orderDate);

        String productId = "6040d6ba1e240556a8b76e8a1";
        Map<String, Integer> orderedProducts = testHMOrderedProduct(productId, 1);
        orderDtoNoId.setOrderedProducts(orderedProducts);

        // mock user entity
        UserEntity userEntity = createUserEntity(userId, "John", "Doe", "jd@mockemail.com",
                "jd", "qwerty", "40722112211", "male", "Toamnei nr.1",
                "Las Vegas", "Nevada", "440055");
        userEntity.setEnabled(true);
        doReturn(Optional.of(userEntity)).when(userRepository).findById(userId);

        // mock product entity
        ProductEntity productEntity = createProductEnity(productId, "Bike", "Mountain Bike", 5000.1,
                4.5, 26, null, "DHS");
        doReturn(Optional.of(productEntity)).when(productRepository).findById(productId);

        // mock saved order entity
        String orderId = "6037a0ab9cfa0f22a397ac4c";
        OrderEntity orderEntity = testOrderEntity(orderId, orderDtoNoId.getOrderValue(), userEntity);
        orderEntity.setOrderDate(orderDate);
        orderEntity.setOrderedProducts(orderDtoNoId.getOrderedProducts());
        doReturn(orderEntity).when(orderRepository).save(any(OrderEntity.class));

        // mock generated PDF
        doReturn(new ByteArrayOutputStream()).when(invoiceGeneratorService).createPDF(any(OrderEntity.class), anyList());

        doReturn("Invoice").when(messageSource)
                .getMessage(eq("invoice.base.name"), isNull(), any(Locale.class));
        doReturn("Order created successfully").when(messageSource)
                .getMessage(eq("order.created.subject"), isNull(), any(Locale.class));

        // WHEN
        OrderDto createdOrderDto = orderService.createOrder(orderDtoNoId);


        // THEN

        // verify order save
        verify(orderRepository).save(orderEntityCaptor.capture());
        OrderEntity savedOrder = orderEntityCaptor.getValue();
        assertThat(savedOrder).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(orderEntity);

        // verify invoice creation
        verify(invoiceGeneratorService).createPDF(any(OrderEntity.class), productLineListCaptor.capture());
        List<ProductLine> productLines = productLineListCaptor.getValue();
        assertThat(productLines).hasSize(1);

        // verify invoice data
        ProductLine productLine = productLines.get(0);
        assertEquals(productId, productLine.getId(),
                "The product ID written to the invoice should be the same as the one from the order!");
        assertEquals(productEntity.getDescription(), productLine.getDescription(),
                "The product description written to the invoice should be the same as the one from product entity!");
        assertEquals(savedOrder.getOrderedProducts().get(productId), productLine.getQuantity(),
                "The ordered quantity written to the invoice should be the same as the one from the order!");
        assertEquals(productEntity.getPrice(), productLine.getUnitPrice(),
                "The product price written to the invoice should be the same as the one from the product entity!");

        // verify email sending
        verify(emailService).sendMessage(eq(userEntity.getEmail()), eq("Order created successfully"),
                eq("order-created"), anyMap(), anyMap());

        // verify order DTO
        assertNotNull(createdOrderDto);
        assertThat(createdOrderDto).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(orderDtoNoId);
    }

    @Test
    void updateOrder() {

        String orderId = "savedOrderId";
        OrderEntity orderFound = testOrderEntity("savedOrderId", 100.1, null);
        OrderEntity savedOrder = testOrderEntity("updatedOrderId", 1.1, null);

        final String firstProductId = "firstProductId";
        final ProductEntity firstProduct = ProductTestUtils.createProductEnity(firstProductId, null, null,
                null, null, 5, null, null);
        final String secondProductId = "secondProductId";
        final ProductEntity secondProduct = ProductTestUtils.createProductEnity(secondProductId, null, null,
                null, null, 5, null, null);
        final String thirdProductId = "thirdProductId";
        final ProductEntity thirdProduct = ProductTestUtils.createProductEnity(thirdProductId, null, null,
                null, null, 5, null, null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderFound));
        when(productRepository.findById(firstProductId)).thenReturn(Optional.of(firstProduct));
        when(productRepository.findById(secondProductId)).thenReturn(Optional.of(secondProduct));
        when(productRepository.findById(thirdProductId)).thenReturn(Optional.of(thirdProduct));
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
