package eu.accesa.onlinestore.integration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.controller.OrderController;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.service.implementation.OrderServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerIntegrationTest extends WebSecurityConfigurerAdapter {
    // the path to the JSON file
    private final File ORDER_DATA_JSON = Paths.get("src", "test", "resources", "data", "OrderData.json").toFile();

    // used to load a JSON file into a list of Products
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MongoTemplate mongoTemplate; // makes the interaction with the embedded MongoDB much easier
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderServiceImpl orderService;

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
    public void findById() throws Exception {
        String orderId = "orderId";

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("orderId")));
    }

}
