package eu.accesa.onlinestore.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.entity.CartEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DataMongoTest
class CartRepositoryTest {

    private final File CART_DATA_JSON = Paths.get("src", "test", "resources", "data",
            "CartData.json").toFile();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MongoTemplate mongoTemplate; // makes the interaction with the embedded MongoDB much easier

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    public void setUp() throws IOException {
        // deserialize the JSON file to an array of carts
        CartEntity[] carts = objectMapper.readValue(CART_DATA_JSON, CartEntity[].class);

        // load each user into embedded MongoDB
        Arrays.stream(carts).forEach(mongoTemplate::save);
    }

    @AfterEach
    public void tearDown() {
        // drops the carts collection
        mongoTemplate.dropCollection("carts");
    }

    @Test
    void testFindByUserId() {
        // GIVEN
        final String userId = "testtest";

        // WHEN
        final Optional<CartEntity> cartOptional = cartRepository.findByUserId(userId);

        // THEN
        cartOptional.ifPresentOrElse(
                cartEntity -> assertEquals(userId, cartEntity.getUserId()),
                () -> fail("A cart with userId = " + userId + " should be found in the database!"));
    }

    @Test
    void testSave() {
        // GIVEN
        final CartEntity cartEntity = new CartEntity();
        cartEntity.setId("1234567");
        cartEntity.setUserId("user id");

        final Map<String, Integer> products = new HashMap<>();
        products.put("147", 1);
        products.put("258", 2);
        products.put("359", 3);
        cartEntity.setProducts(products);

        // WHEN
        final CartEntity savedCart = cartRepository.save(cartEntity);

        // THEN
        final Optional<CartEntity> loadedCarts = cartRepository.findById(cartEntity.getId());
        loadedCarts.ifPresentOrElse(
                cart -> assertThat(cart).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(savedCart),
                () -> fail("A new cart should have been saved to the database!"));
    }

    @Test
    void testUpdate() {
        // GIVEN
        final String id = "1234567";
        final CartEntity cartEntity = cartRepository.findById(id).get();

        final String newUserId = "1234";
        cartEntity.setUserId(newUserId);

        // WHEN
        final CartEntity updatedCart = cartRepository.save(cartEntity);

        // THEN
        assertThat(updatedCart).usingRecursiveComparison()
                .ignoringFields("products")
                .isEqualTo(cartEntity);
        assertEquals(newUserId, updatedCart.getUserId());
    }

    @Test
    void testDelete() {
        // GIVEN
        final String id = "1234567";

        // WHEN
        cartRepository.deleteById(id);

        // THEN
        assertEquals(1, cartRepository.findAll().size());
    }
}
