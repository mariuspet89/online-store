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
import eu.accesa.onlinestore.service.OrderService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final ModelMapper mapper;

    private final EmailServiceImpl emailService;
    private final PdfGeneratorServiceImpl pdfGeneratorService;

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(ModelMapper mapper, EmailServiceImpl emailService, PdfGeneratorServiceImpl pdfGeneratorService,
                            OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.mapper = mapper;
        this.emailService = emailService;
        this.pdfGeneratorService = pdfGeneratorService;
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
        LOGGER.info("Order Service: creating order...");

        // verify if user exists
        String userId = orderDtoNoId.getUserId();
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getName(), " UserID ", userId));

        // table data for the order invoice
        List<ProductLine> productLines = new ArrayList<>();

        // verify that products exist
        orderDtoNoId.getOrderedProducts().forEach((productId, value) -> {
            Optional<ProductEntity> optionalProductEntity = productRepository.findById(productId);
            if (optionalProductEntity.isEmpty()) {
                throw new EntityNotFoundException(ProductEntity.class.getName(), "ProductId", productId);
            } else {
                ProductEntity product = optionalProductEntity.get();
                productLines.add(new ProductLine(product.getDescription(), value, product.getPrice()));
            }
        });

        // save order
        OrderEntity orderEntity = mapper.map(orderDtoNoId, OrderEntity.class);
        orderEntity.setUser(userEntity);
        orderEntity = orderRepository.save(orderEntity);

        // prepare template data
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("orderId", orderEntity.getId());
        templateModel.put("orderDate", orderEntity.getOrderDate()
                .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));

        // send confirmation email
        try {
            emailService.sendMessageUsingThymeleafTemplate(userEntity.getEmail(), "Order Placed Successfully",
                    "order-placed", templateModel);
        } catch (MessagingException e) {
            LOGGER.error("The order email could not be placed!");
            LOGGER.error(e.getMessage());
        }

        // generate invoice
        pdfGeneratorService.generateInvoice(orderEntity, productLines);

        // send email with attachment
        emailService.sendMessageWithAttachment(userEntity.getEmail(), "Order Invoice",
                "Please find the invoice generated for your order", "src/main/resources/order-templates/Invoice.pdf");

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
