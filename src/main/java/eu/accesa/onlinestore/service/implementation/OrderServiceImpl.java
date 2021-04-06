package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
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
import eu.accesa.onlinestore.service.OrderService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final ModelMapper mapper;

    private final EmailServiceImpl emailService;
    private final InvoiceGeneratorService invoiceGeneratorService;

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(ModelMapper mapper, EmailServiceImpl emailService,
                            InvoiceGeneratorService invoiceGeneratorService, OrderRepository orderRepository,
                            ProductRepository productRepository, UserRepository userRepository) {
        this.mapper = mapper;
        this.emailService = emailService;
        this.invoiceGeneratorService = invoiceGeneratorService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<OrderDto> findAll() {
        LOGGER.info("Service: getting  all  order");
        List<OrderEntity> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderEntity -> mapper.map(orderEntity, OrderDto.class))
                .collect(Collectors.toList());
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
        return orders.stream()
                .map(orderEntity -> mapper.map(orderEntity, OrderDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto createOrder(OrderDtoNoId orderDtoNoId) {
        LOGGER.info("Order Service: creating order...");

        //Retrieve the user or throw exception if user doesn't exist
        String userId = orderDtoNoId.getUserId();

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getName(), " UserID ", userId));

        //Creating table data for the order invoice
        List<ProductLine> productLines = new ArrayList<>();

        //Verifying that products exist and if they do, adding them to productLines
        orderDtoNoId.getOrderedProducts().forEach((productId, value) -> {
            Optional<ProductEntity> optionalProductEntity = productRepository.findById(productId);
            if (optionalProductEntity.isEmpty()) {
                throw new EntityNotFoundException(ProductEntity.class.getName(), "ProductId", productId);
            } else {
                ProductEntity product = optionalProductEntity.get();
                productLines.add(new ProductLine(product.getId(), product.getDescription(), value, product.getPrice()));
            }
        });

        //Saving order
        OrderEntity orderEntity = mapper.map(orderDtoNoId, OrderEntity.class);
        orderEntity.setUser(userEntity);
        orderEntity = orderRepository.save(orderEntity);

        //Preparing template data
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("orderId", orderEntity.getId());
        templateModel.put("orderDate", orderEntity.getOrderDate()
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));

        //Generating invoice
        ByteArrayOutputStream generatedInvoice = invoiceGeneratorService.createPDF(orderEntity, productLines);
        ByteArrayInputStream invoiceAsBytes = new ByteArrayInputStream(generatedInvoice.toByteArray());

        //Preparing attachments
        Map<String, ByteArrayInputStream> attachments = new HashMap<>();
        attachments.put("Invoice.pdf", invoiceAsBytes);

        //Sending email with invoice attached
        emailService.sendMessage(userEntity.getEmail(), "Order Created Successfully",
                "order-created", templateModel, attachments);

        //Returning created order DTO
        return mapper.map(orderEntity, OrderDto.class);
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

    @Override
    public void deleteOrder(String id) {
        LOGGER.info("Deleting order with ID = {}", id);
        orderRepository.deleteById(id);
    }
}
