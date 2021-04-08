package eu.accesa.onlinestore.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.entity.AddressEntity;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
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

//Creates an embedded MongoDB instance and loads the subset of the Spring configuration that supports MongoDB
@DataMongoTest
public class OrderRepositoryTest {
    // the path to the JSON file
    private final File ORDER_DATA_JSON = Paths.get("src", "test", "resources", "data", "OrderData.json").toFile();

    // used to load a JSON file into a list of Products
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MongoTemplate mongoTemplate; // makes the interaction with the embedded MongoDB much easier

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() throws IOException {
        // deserialize the JSON file to an array of products

        OrderEntity[] orders = objectMapper.readValue(ORDER_DATA_JSON, OrderEntity[].class);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // load each product into embedded MongoDB
        Arrays.stream(orders).forEach(mongoTemplate::save);
    }

    @AfterEach
    public void tearDown() {
        // drop the products collection
        mongoTemplate.dropCollection("orders");
    }

    @Test
    public void testFindAllSuccess() {
        // WHEN
        List<OrderEntity> orders = orderRepository.findAll();

        // THEN
        assertEquals(2, orders.size(), "findAll() should return 2 orders!");
    }

    @Test
    public void testFindByIdSucces() {
        //GIVEN
        final String id = "6038ae272c4f617114584428";

        //WHEN
        Optional<OrderEntity> order = orderRepository.findById(id);

        //THEN
        assertTrue(order.isPresent(), "An order with Id: " + id + " should exist");
        order.ifPresent(orderEntity -> {
            assertEquals(id, orderEntity.getId());
            assertEquals(12, orderEntity.getOrderValue());
            assertEquals("603648273ed85832b440eb99", orderEntity.getUser().getId());
            assertEquals("Lilah", orderEntity.getUser().getFirstName());
            assertEquals("Rozier", orderEntity.getUser().getLastName());
            assertEquals("Cluj-Napoca", orderEntity.getUser().getAddressEntity().getCity());
            assertEquals(2021, orderEntity.getOrderDate().getYear());
            assertEquals(2, orderEntity.getOrderDate().getMonthValue());
            assertEquals(26, orderEntity.getOrderDate().getDayOfMonth());
        });
    }

    @Test
    public void testFindByFailure() {
        //GIVEN
        final String id = "fakeId";

        //WHEN
        Optional<OrderEntity> order = orderRepository.findById(id);

        // THEN
        assertFalse(order.isPresent(), "An order with ID = " + id + " should not be present!");

    }

    @Test
    public void testGetOrderEntitiesByUserIdSuccess() {
        // GIVEN
        final String userId = "604107dde3835d7496be4e3d";

        //WHEN
        List<OrderEntity> orders = orderRepository.getOrderEntitiesByUserId(userId);

        //THEN
        assertNotNull(orders, "List is empty");
        assertEquals(1, orders.size());
        OrderEntity orderFound = orders.get(0);
        assertTrue(orders.contains(orderFound));
        assertEquals("6037a0ab9cfa0f22a397ac4c", orderFound.getId());
    }

    @Test
    public void testDeleteSuccess() {
        // GIVEN
        final String id = "6037a0ab9cfa0f22a397ac4c";

        // WHEN
        orderRepository.deleteById(id);

        // THEN
        assertEquals(1, orderRepository.findAll().size());
    }

    @Test
    public void testUpdateSuccess() {
        // GIVEN
        final String id = "6037a0ab9cfa0f22a397ac4c";
        final OrderEntity order = orderRepository.findById(id).get();
        order.setOrderValue(1.1);

        // WHEN
        final OrderEntity updatedOrder = orderRepository.save(order);

        // THEN
        assertThat(updatedOrder).usingRecursiveComparison()
                .ignoringFields("orderValue")
                .isEqualTo(order);
        assertEquals(1.1, updatedOrder.getOrderValue());
    }

    @Test
    public void testSave() {
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
        assertTrue(loadedOrder.isPresent());
        loadedOrder.ifPresent(order ->
                assertThat(order).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(savedOrder));
    }
}
