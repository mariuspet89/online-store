package eu.accesa.onlinestore.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.dto.OrderDto;
import eu.accesa.onlinestore.model.dto.OrderDtoNoId;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.utils.mongodb.MongoDataFile;
import eu.accesa.onlinestore.utils.mongodb.MongoSpringExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static eu.accesa.onlinestore.utils.OrderTestUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MongoSpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@TestPropertySource(properties = {
        "JWT_ISSUER=eu.accesa.onlinestore",
        "JWT_SECRET=L6avrMtXQIhTP4tGc6qlz02RV46DCCEkQY25EOce3PAyJZkhD93ViPI44t9n2DP"
})
public class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * MongoSpringExtension method that returns the autowired MongoTemplate to use for MongoDB interactions.
     *
     * @return The autowired MongoTemplate instance.
     */
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Test
    @WithMockUser
    @DisplayName("GET /orders/orderId - Found")
    @MongoDataFile(value = "OrderData.json", classType = OrderEntity.class, collectionName = "orders")
    void findById() throws Exception {
        //Given
        String orderId = "6037a0ab9cfa0f22a397ac4c";

        //When,Then
        mockMvc.perform(get("/orders/{id}", orderId))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(orderId)))
                .andExpect(jsonPath("$.orderValue", is(2367.0)))
                .andExpect(jsonPath("$.userId", is("604107dde3835d7496be4e3d")));
    }

    @Test
    @WithMockUser
    @DisplayName("GET //orders/user/{userId} - Found")
    @MongoDataFile(value = "OrderData.json", classType = OrderEntity.class, collectionName = "orders")
    void findByUserId() throws Exception {
        String userId = "603648273ed85832b440eb99";
        mockMvc.perform(get("/orders/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id").isNotEmpty())
                .andExpect(jsonPath("$[*].id").value(containsInAnyOrder("6038ae272c4f617114584428")))
                .andExpect(jsonPath("$[*].orderValue").value(containsInAnyOrder(12.0)))
                .andExpect(jsonPath("$[*].userId").isNotEmpty())
                .andExpect(jsonPath("$[*].userId").value(containsInAnyOrder("603648273ed85832b440eb99")));
    }

    @Test
    @WithMockUser
    @DisplayName("GET //orders/getAll - Found")
    @MongoDataFile(value = "OrderData.json", classType = OrderEntity.class, collectionName = "orders")
    void findAll() throws Exception {
        mockMvc.perform(get("/orders/getAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id").isNotEmpty())
                .andExpect(jsonPath("$[*].id").value(containsInAnyOrder("6038ae272c4f617114584428", "6037a0ab9cfa0f22a397ac4c")))
                .andExpect(jsonPath("$[*].orderValue").value(containsInAnyOrder(12.0, 2367.0)))
                .andExpect(jsonPath("$[*].userId").isNotEmpty())
                .andExpect(jsonPath("$[*].userId").value(containsInAnyOrder("604107dde3835d7496be4e3d", "603648273ed85832b440eb99")));
    }

    @Test
    @WithMockUser
    @DisplayName("POST //orders - Order created")
    @MongoDataFile(value = "OrderData.json", classType = OrderEntity.class, collectionName = "orders")
    void createOrderFailure() throws Exception {
        // Setup mocked service
        Map<String, Integer> orderedProducts = testHMOrderedProduct("6034068975bb0d4088a441c2", 1);
        OrderDtoNoId orderToBeSavedNoId = testOrderDtoNoId(1.1, "603648273ed85832b440eb99");
        orderToBeSavedNoId.setOrderedProducts(orderedProducts);
        String expectedMessage = "UserEntity with UserID = " + orderToBeSavedNoId.getUserId() + " not found";
        ResultActions result = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(orderToBeSavedNoId)));

        // expected status is set to 404 because there is no embedded UserDb set and userService.findById(userId) will throw an EntityNotFoundException
        result.andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT //orders - Order updated")
    @MongoDataFile(value = "OrderData.json", classType = OrderEntity.class, collectionName = "orders")
    void updateOrder() throws Exception {
        String id = "6037a0ab9cfa0f22a397ac4c";
        OrderDto orderToPut = testOrderDto("6037a0ab9cfa0f22a397ac4c", 2.1, "userId1");
        final ObjectMapper objectMapper = new ObjectMapper();
        final String orderToPutJson = objectMapper.writeValueAsString(orderToPut);
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/orders/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderToPutJson));
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("6037a0ab9cfa0f22a397ac4c")))
                .andExpect(jsonPath("$.orderValue", is(2.1)));
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

