package eu.accesa.onlinestore.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.entity.CartEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@Disabled
public class CartRepositoryTest {

    private final File CART_DATA_JSON = Paths.get("src", "test", "resources", "data",
            "CartData.json").toFile();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MongoTemplate mongoTemplate; // makes the interaction with the embedded MongoDB much easier

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    public void setUp() throws IOException {
        // deserialize the JSON file to an array of users

        CartEntity[] carts = objectMapper.readValue(CART_DATA_JSON, CartEntity[].class);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // load each user into embedded MongoDB
        Arrays.stream(carts).forEach(mongoTemplate::save);
    }

    @AfterEach
    public void tearDown() {
        // drop the users collection
        mongoTemplate.dropCollection("carts");

    }

    @Test
    public void testDeleteSuccess() {
        // GIVEN
        final String id = "1234567";

        // WHEN
        cartRepository.deleteById(id);

        // THEN
        assertEquals(1, cartRepository.findAll().size());
    }

    @Test
    public void testUpdateSuccess() {
        // GIVEN

        final String id = "1234567";
        final CartEntity cartEntity = cartRepository.findById(id).get();
        cartEntity.setUserId("1234");

        // WHEN
        final CartEntity updatedCart = cartRepository.save(cartEntity);

        // THEN
        assertThat(updatedCart).usingRecursiveComparison()
                .ignoringFields("products")
                .isEqualTo(cartEntity);
        assertEquals("1234", updatedCart.getUserId());
    }

    @Test
    public void testSave() {

        final CartEntity cartEntity = new CartEntity();

        cartEntity.setId("1234567");
        cartEntity.setUserId("user id");

        final CartEntity savedCart = cartRepository.save(cartEntity);

        final Optional<CartEntity> loadedCarts
                = cartRepository.findById(cartEntity.getId());
        assertTrue(loadedCarts.isPresent());
        loadedCarts.ifPresent(cart ->
                assertThat(cart).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(savedCart));

    }

    @Test
    public void testFindCartEntityByUserId() {

        final String userId = "testtest";
        final Optional<CartEntity> cart =
                cartRepository.findCartEntityByUserId(userId);

        assertEquals(userId, cart.get().getUserId());
    }
}
