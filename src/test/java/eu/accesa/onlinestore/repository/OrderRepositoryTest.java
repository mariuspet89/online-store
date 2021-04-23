package eu.accesa.onlinestore.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.entity.AddressEntity;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

// Creates an embedded MongoDB instance and loads the subset of the Spring configuration that supports MongoDB
@DataMongoTest
class OrderRepositoryTest {

    // the path to the JSON file
    private final File ORDER_DATA_JSON = Paths.get("src", "test", "resources", "data",
            "OrderData.json").toFile();

    // used to load a JSON file into a list of Orders
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MongoTemplate mongoTemplate; // makes the interaction with the embedded MongoDB much easier

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() throws IOException {
        // deserialize the JSON file to an array of orders
        final OrderEntity[] orders = objectMapper.readValue(ORDER_DATA_JSON, OrderEntity[].class);

        // load each product into embedded MongoDB
        Arrays.stream(orders).forEach(mongoTemplate::save);
    }

    @AfterEach
    public void tearDown() {
        // drop the products collection
        mongoTemplate.dropCollection("orders");
    }

    @Test
    void testFindAll() {
        // WHEN
        final List<OrderEntity> orders = orderRepository.findAll();

        // THEN
        assertEquals(2, orders.size(), "findAll() should return 2 orders!");
    }

    @Test
    void testFindById() {
        // GIVEN
        final String id = "6038ae272c4f617114584428";

        // WHEN
        final Optional<OrderEntity> order = orderRepository.findById(id);

        // THEN
        order.ifPresentOrElse(
                orderEntity -> {
                    assertEquals(id, orderEntity.getId());
                    assertEquals(12, orderEntity.getOrderValue());

                    final UserEntity userEntity = orderEntity.getUser();
                    assertEquals("603648273ed85832b440eb99", userEntity.getId());
                    assertEquals("Lilah", userEntity.getFirstName());
                    assertEquals("Rozier", userEntity.getLastName());
                    assertEquals("Cluj-Napoca", userEntity.getAddressEntity().getCity());

                    final LocalDateTime orderDate = orderEntity.getOrderDate();
                    assertEquals(2021, orderDate.getYear());
                    assertEquals(2, orderDate.getMonthValue());
                    assertEquals(26, orderDate.getDayOfMonth());
                },
                () -> fail("An order with orderId = " + id + " should be found in the database!"));
    }

    @Test
    void testFindByUserId() {
        // GIVEN
        final String userId = "604107dde3835d7496be4e3d";

        // WHEN
        final List<OrderEntity> orders = orderRepository.findByUserId(userId);

        // THEN
        orders.forEach(orderEntity -> assertEquals(userId, orderEntity.getUser().getId()));
    }

    @Test
    void testSave() {
        // GIVEN
        final UserEntity userEntity = new UserEntity();
        userEntity.setId("60377ec00e2cb07c9a3811d3");
        userEntity.setFirstName("New");
        userEntity.setLastName("User");
        userEntity.setEmail("new@user.com");
        userEntity.setUsername("newUser");
        userEntity.setPassword("newPassword");
        userEntity.setTelephone("123456789");
        userEntity.setSex("Male");

        final AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddress("Strada Primaverii");
        addressEntity.setCity("Cluj-Napoca");
        addressEntity.setCounty("Cluj");
        addressEntity.setPostalCode("123456");
        userEntity.setAddressEntity(addressEntity);

        final Map<String, Integer> orderedProducts = new HashMap<>();
        orderedProducts.put("6034068975bb0d4088a441c2", 1);

        final OrderEntity orderToSave = new OrderEntity();
        orderToSave.setOrderValue(123.1);
        orderToSave.setUser(userEntity);
        orderToSave.setOrderDate(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS"))));
        orderToSave.setOrderedProducts(orderedProducts);

        // WHEN
        final OrderEntity savedOrder = orderRepository.save(orderToSave);

        // THEN
        // validate we can get the order from the database
        final Optional<OrderEntity> loadedOrder = orderRepository.findById(savedOrder.getId());
        loadedOrder.ifPresentOrElse(
                order -> assertThat(order).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(savedOrder),
                () -> fail("An new order should have been saved to the database!"));
    }

    @Test
    void testUpdate() {
        // GIVEN
        final String id = "6037a0ab9cfa0f22a397ac4c";
        OrderEntity order = orderRepository.findById(id).get();

        final double newOrderValue = 1.1;
        order.setOrderValue(newOrderValue);

        // WHEN
        final OrderEntity updatedOrder = orderRepository.save(order);

        // THEN
        assertThat(updatedOrder).usingRecursiveComparison()
                .ignoringFields("orderValue")
                .isEqualTo(order);
        assertEquals(newOrderValue, updatedOrder.getOrderValue());
    }

    @Test
    void testDelete() {
        // GIVEN
        final String id = "6037a0ab9cfa0f22a397ac4c";

        // WHEN
        orderRepository.deleteById(id);

        // THEN
        assertEquals(1, orderRepository.findAll().size());
    }
}
