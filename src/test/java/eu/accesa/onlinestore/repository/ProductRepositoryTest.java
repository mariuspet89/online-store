package eu.accesa.onlinestore.repository;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.entity.ProductEntity;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Creates an embedded MongoDB instance and loads the subset of the Spring configuration that supports MongoDB
@DataMongoTest
public class ProductRepositoryTest {
    private final File PRODUCT_DATA_JSON = Paths.get("src", "test", "resources", "data",
            "ProductData.json").toFile();

    // used to load a JSON file into a list of Users
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MongoTemplate mongoTemplate; // makes the interaction with the embedded MongoDB much easier

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() throws IOException {
        // deserialize the JSON file to an array of users

        ProductEntity[] products = objectMapper.readValue(PRODUCT_DATA_JSON, ProductEntity[].class);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // load each user into embedded MongoDB
        Arrays.stream(products).forEach(mongoTemplate::save);
    }

    @AfterEach
    public void tearDown() {
        // drop the users collection
        mongoTemplate.dropCollection("products");

    }

    @Test
    public void testFindAllSuccess() {
        // WHEN
        List<ProductEntity> products = productRepository.findAll();

        // THEN
        assertEquals(2, products.size(), "findAll() should return 2 orders!");
    }

    @Test
    public void testDeleteSuccess() {
        // GIVEN
        final String id = "6040d6ba1e240556a8b76e9b";

        // WHEN
        productRepository.deleteById(id);

        // THEN
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    public void testUpdateSuccess() {
        // GIVEN

        final String id = "6040d6ba1e240556a8b76e9b";
        final ProductEntity productEntity = productRepository.findById(id).get();
        productEntity.setBrand("update Brand");

        // WHEN
        final ProductEntity updatedProduct = productRepository.save(productEntity);

        // THEN
        assertThat(updatedProduct).usingRecursiveComparison()
                .ignoringFields("brand")
                .isEqualTo(productEntity);
        assertEquals("update Brand", updatedProduct.getBrand());
    }

    @Test
    public void testSave() {

        final ProductEntity productEntity = new ProductEntity();

        productEntity.setId("60377ec00e2cb07c9a3812f3");
        productEntity.setName("test save");
        productEntity.setBrand("test save");
        productEntity.setImage("test save");
        productEntity.setPrice(0.0);
        productEntity.setRating(0.0);
        productEntity.setDescription("test save");
        productEntity.setItemsInStock(0);

        final ProductEntity savedProduct = productRepository.save(productEntity);

        final Optional<ProductEntity> loadedProduct
                = productRepository.findById(productEntity.getId());
        assertTrue(loadedProduct.isPresent());

        loadedProduct.ifPresent(product ->
                assertThat(product).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(savedProduct));

    }
    @Test
    public void testFindByNameIsContainingIgnoreCase(){

        final String name = "BICICLETĂ MTB E-ST 900 27,5 PLUS PORTOCALIU ROCKRIDER";
        final List <ProductEntity> products =
                productRepository.findByNameIsContainingIgnoreCase(name);

        assertEquals(name, products.get(0).getName());
    }

}
