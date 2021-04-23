package eu.accesa.onlinestore.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.*;

// Creates an embedded MongoDB instance and loads the subset of the Spring configuration that supports MongoDB
@DataMongoTest
class ProductRepositoryTest {

    private final File PRODUCT_DATA_JSON = Paths.get("src", "test", "resources", "data",
            "ProductData.json").toFile();

    // used to load a JSON file into a list of Products
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MongoTemplate mongoTemplate; // makes the interaction with the embedded MongoDB much easier

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() throws IOException {
        // deserialize the JSON file to an array of products
        final ProductEntity[] products = objectMapper.readValue(PRODUCT_DATA_JSON, ProductEntity[].class);

        // load each user into embedded MongoDB
        Arrays.stream(products).forEach(mongoTemplate::save);
    }

    @AfterEach
    public void tearDown() {
        // drop the users collection
        mongoTemplate.dropCollection("products");
    }

    @Test
    void testFindAll() {
        // WHEN
        final List<ProductEntity> products = productRepository.findAll();

        // THEN
        assertEquals(2, products.size(), "findAll() should have returned 2 products!");
    }

    @Test
    void testFindByNameIsContainingIgnoreCase() {
        // GIVEN
        final String text = "ROCKRIDER";

        // WHEN
        final List<ProductEntity> products =
                productRepository.findByNameIsContainingIgnoreCase(text);

        // THEN
        products.forEach(productEntity -> assertTrue(productEntity.getName().contains(text),
                "The product name should contain the \"" + text + "\" text sequence"));
    }

    @Test
    void testSave() {
        // GIVEN
        final ProductEntity productEntity = new ProductEntity();

        productEntity.setId("60377ec00e2cb07c9a3812f3");
        productEntity.setName("test save");
        productEntity.setBrand("test save");
        productEntity.setImage("test save");
        productEntity.setPrice(0.0);
        productEntity.setRating(0.0);
        productEntity.setDescription("test save");
        productEntity.setItemsInStock(0);

        // WHEN
        final ProductEntity savedProduct = productRepository.save(productEntity);

        // THEN
        final Optional<ProductEntity> loadedProduct
                = productRepository.findById(productEntity.getId());
        assertTrue(loadedProduct.isPresent());

        loadedProduct.ifPresentOrElse(
                product -> assertThat(product).usingRecursiveComparison()
                        .ignoringFields("id")
                        .isEqualTo(savedProduct),
                () -> fail("An new product should have been saved to the database!"));
    }

    @Test
    void testUpdateSuccess() {
        // GIVEN
        final String id = "6040d6ba1e240556a8b76e9b";
        final String newBrand = "Updated Brand";

        final ProductEntity productEntity = productRepository.findById(id).get();
        productEntity.setBrand(newBrand);

        // WHEN
        final ProductEntity updatedProduct = productRepository.save(productEntity);

        // THEN
        assertThat(updatedProduct).usingRecursiveComparison()
                .ignoringFields("brand")
                .isEqualTo(productEntity);
        assertEquals(newBrand, updatedProduct.getBrand());
    }

    @Test
    void testDelete() {
        // GIVEN
        final String id = "6040d6ba1e240556a8b76e9b";

        // WHEN
        productRepository.deleteById(id);

        // THEN
        assertEquals(1, productRepository.findAll().size());
    }
}
