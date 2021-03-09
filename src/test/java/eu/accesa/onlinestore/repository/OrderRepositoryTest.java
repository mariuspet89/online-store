package eu.accesa.onlinestore.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Creates an embedded MongoDB instance and loads the subset of the Spring configuration that supports Mon
@DataMongoTest
class OrderRepositoryTest {
    // the path to the JSON file
    private final File ORDER_DATA_JSON = Paths.get("src", "test", "resources", "data", "OrderData.json").toFile();

    // used to load a JSON file into a list of Users
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MongoTemplate mongoTemplate; // makes the interaction with the embedded MongoDB much easier

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() throws IOException {
        // deserialize the JSON file to an array of users

        OrderEntity[] orders = objectMapper.readValue(ORDER_DATA_JSON, OrderEntity[].class);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // load each user into embedded MongoDB
        Arrays.stream(orders).forEach(mongoTemplate::save);
    }

    @AfterEach
    void tearDown() {
        // drop the users collection
        mongoTemplate.dropCollection("orders");
    }

    @Test
    void testFindAllSuccess() {
        // WHEN
        List<OrderEntity> orders = orderRepository.findAll();

        // THEN
        assertEquals(2, orders.size(), "findAll() should return 2 orders!");
    }
    @Test
    void testFindAllFailure() {
        // WHEN
        List<OrderEntity> orders = orderRepository.findAll();

        // THEN
        assertEquals(20, orders.size(), "findAll() should return 20 orders!");
    }
    @Test
    void testFindByIdSucces(){
        //GIVEN
        final String id="6038ae272c4f617114584428";

        //WHEN
        Optional<OrderEntity> order=orderRepository.findById(id);

        //THEN
        assertTrue(order.isPresent(),"An order with Id: "+id+" should exist");
        order.ifPresent(orderEntity -> {
            assertEquals(id,orderEntity.getId());
            assertEquals(2367,orderEntity.getOrderValue());
            assertEquals("603648273ed85832b440eb9c",orderEntity.getUser().getId());

        });
    }
}