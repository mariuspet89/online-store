package eu.accesa.onlinestore.integration;

import eu.accesa.onlinestore.model.entity.ProductEntity;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MongoSpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "JWT_ISSUER=eu.accesa.onlinestore",
        "JWT_SECRET=L6avrMtXQIhTP4tGc6qlz02RV46DCCEkQY25EOce3PAyJZkhD93ViPI44t9n2DP"
})
public class ProductIntegrationTest {

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
    @DisplayName("GET /products/productId - Found")
    @MongoDataFile(value = "ProductData.json", classType = ProductEntity.class, collectionName = "products")
    void testFindById() throws Exception {
        // GIVEN
        final String productId = "6040d6ba1e240556a8b76e9b";

        mockMvc.perform(get("/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(productId));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /products/productId - Success")
    @MongoDataFile(value = "ProductData.json", classType = ProductEntity.class, collectionName = "products")
    void testDelete() throws Exception {
        // GIVEN
        final String productId = "6040d6ba1e240556a8b76e9b";

        // WHEN, THEN
        mockMvc.perform(delete("/products/{id}", productId))
                .andExpect(status().isOk());
    }
}
