package eu.accesa.onlinestore.integration;

import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.utils.mongoDbUtils.MongoDataFile;
import eu.accesa.onlinestore.utils.mongoDbUtils.MongoSpringExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith({SpringExtension.class,MongoSpringExtension.class})
@SpringBootTest
@AutoConfigureMockMvc

public class OrderControllerIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MockMvc mockMvc;
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
    @Test
    @DisplayName("GET /orders/orderId - Found")
    @MongoDataFile(value = "OrderData.json", classType = OrderEntity.class, collectionName = "orders")
    public void findById() throws Exception {
        String orderId = "orderId";

        mockMvc.perform(get("/orders/{id}", orderId))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("orderId")));
    }
}
