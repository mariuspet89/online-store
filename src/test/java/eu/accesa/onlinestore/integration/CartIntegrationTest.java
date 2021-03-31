package eu.accesa.onlinestore.integration;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.dto.CartDtoNoId;
import eu.accesa.onlinestore.model.entity.CartEntity;
import eu.accesa.onlinestore.utils.mongodb.MongoDataFile;
import eu.accesa.onlinestore.utils.mongodb.MongoSpringExtension;
import org.junit.jupiter.api.Disabled;
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

import java.util.HashMap;

import static eu.accesa.onlinestore.utils.CartTestUtils.createCartDtoNoId;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled
@ExtendWith(MongoSpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "JWT_ISSUER=eu.accesa.onlinestore",
        "JWT_SECRET=L6avrMtXQIhTP4tGc6qlz02RV46DCCEkQY25EOce3PAyJZkhD93ViPI44t9n2DP"
})
public class CartIntegrationTest {

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
    @DisplayName("GET /carts/cartId - Found")
    @MongoDataFile(value = "CartData.json", classType = CartEntity.class, collectionName = "carts")
    void findById() throws Exception {
        //Given
        String cartId = "1234567";

        //When, Then
        mockMvc.perform(get("/carts/{id}", cartId))
                //Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(cartId)))
                .andExpect(jsonPath("$.userId", is("testtest")));
    }

    @Test
    @WithMockUser
    @DisplayName("GET //carts/users/{userId} - Found")
    @MongoDataFile(value = "CartData.json", classType = CartEntity.class, collectionName = "carts")
    void findByUserId() throws Exception {
        String userId = "testtest";
        ResultActions perform = mockMvc.perform(get("/carts/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.id").value("1234567"))
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.userId").value("testtest"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST //carts - Cart created")
    @MongoDataFile(value = "CartData.json", classType = CartEntity.class, collectionName = "carts")
    void createCart() throws Exception {
        // Setup mocked service
        HashMap<String, Integer> cartProducts = new HashMap<>();
        cartProducts.put("6040d6ba1e240556a8b76ec0", 2);
        CartDtoNoId cartToBeSavedNoId = createCartDtoNoId("testtest", cartProducts);
        String expectedMessage = "UserEntity with UserID = " + cartToBeSavedNoId.getUserId() + " not found";
        ResultActions result = mockMvc.perform(post("/carts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cartToBeSavedNoId)));
        result.andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT //carts - Updated created")
    @MongoDataFile(value = "CartData.json", classType = CartEntity.class, collectionName = "carts")
    void updateCart() throws Exception {
        HashMap<String, Integer> cartProducts = new HashMap<>();
        cartProducts.put("6040d6ba1e240556a8b76ec0", 222);
        String cartId = "1234567";
        CartDtoNoId cartDtoNoId = createCartDtoNoId("testtest22", cartProducts);
        final ResultActions resultActions = mockMvc.perform(put("/carts/{id}", cartId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(cartDtoNoId)));
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1234567")))
                .andExpect(jsonPath("$.userId", is("testtest22")));

    }

    @Test
    @WithMockUser
    @DisplayName("DELETE //carts - Cart deleted")
    @MongoDataFile(value = "CartData.json", classType = CartEntity.class, collectionName = "carts")
    void testDeleteCart() throws Exception {
        // GIVEN
        final String cartId = "1234567";

        // WHEN, THEN
        mockMvc.perform(delete("/carts/{id}", cartId))
                .andExpect(status().isOk());
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
