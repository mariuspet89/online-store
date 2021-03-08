package eu.accesa.onlinestore.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.entity.AddressEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;
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
import static org.junit.jupiter.api.Assertions.*;


// Creates an embedded MongoDB instance and loads the subset of the Spring configuration that supports MongoDB.
@DataMongoTest
class UserRepositoryTest {

    // the path to the JSON file
    private final File USER_DATA_JSON = Paths.get("src", "test", "resources", "data", "UserData.json").toFile();

    // used to load a JSON file into a list of Users
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MongoTemplate mongoTemplate; // makes the interaction with the embedded MongoDB much easier

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws IOException {
        // deserialize the JSON file to an array of users
        UserEntity[] users = objectMapper.readValue(USER_DATA_JSON, UserEntity[].class);

        // load each user into embedded MongoDB
        Arrays.stream(users).forEach(mongoTemplate::save);
    }

    @AfterEach
    void tearDown() {
        // drop the users collection
        mongoTemplate.dropCollection("users");
    }

    @Test
    void testFindAllSuccess() {
        // WHEN
        List<UserEntity> users = userRepository.findAll();

        // THEN
        assertEquals(2, users.size(), "findAll() should return 2 users!");
    }

    @Test
    void testFindByIdSuccess() {
        // GIVEN
        final String id = "604107dde3835d7496be4e3d";

        // WHEN
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);

        // THEN
        assertTrue(userEntityOptional.isPresent(), "A user with ID = " + id + " should be present!");
        userEntityOptional.ifPresent(user -> {
            assertEquals(id, user.getId());
            assertEquals("john", user.getFirstName());
            assertEquals("travolta", user.getLastName());
            assertEquals("johny@travolta.com", user.getEmail());
            assertEquals("johnytravolta", user.getUsername());
            assertEquals("123456", user.getTelephone());
            assertEquals("Mr", user.getSex());
            assertEquals("johny123", user.getPassword());

            final AddressEntity addressEntity = user.getAddress();
            assertNotNull(addressEntity);
            assertEquals("sunste blvd, nr 13", addressEntity.getAddress());
            assertEquals("LA", addressEntity.getCity());
            assertEquals("california", addressEntity.getCounty());
            assertEquals("542545", addressEntity.getPostalCode());

            assertTrue(user.getEnabled());
        });
    }

    @Test
    void testFindByIdFailure() {
        // GIVEN
        final String id = "fakeId";

        // WHEN
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);

        // THEN
        assertFalse(userEntityOptional.isPresent(), "A user with ID = " + id + " should not be present!");
    }

    @Test
    void testFindByUsernameSuccess() {
        // GIVEN
        final String username = "johnytravolta";

        // WHEN
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);

        // THEN
        assertTrue(userEntityOptional.isPresent(), "A user with username = " + username + " should be present!");
        userEntityOptional.ifPresent(user -> {
            assertEquals("604107dde3835d7496be4e3d", user.getId());
            assertEquals("john", user.getFirstName());
            assertEquals("travolta", user.getLastName());
            assertEquals("johny@travolta.com", user.getEmail());
            assertEquals(username, user.getUsername());
            assertEquals("123456", user.getTelephone());
            assertEquals("Mr", user.getSex());
            assertEquals("johny123", user.getPassword());

            final AddressEntity addressEntity = user.getAddress();
            assertNotNull(addressEntity);
            assertEquals("sunste blvd, nr 13", addressEntity.getAddress());
            assertEquals("LA", addressEntity.getCity());
            assertEquals("california", addressEntity.getCounty());
            assertEquals("542545", addressEntity.getPostalCode());

            assertTrue(user.getEnabled());
        });
    }

    @Test
    void testSave() {
        // GIVEN
        final UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("New");
        userEntity.setLastName("User");
        userEntity.setEmail("new@user.com");
        userEntity.setUsername("newUser");
        userEntity.setPassword("newPassword");
        userEntity.setTelephone("123456789");
        userEntity.setSex("Wild");

        final AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddress("Strada Primaverii");
        addressEntity.setCity("Cluj-Napoca");
        addressEntity.setCounty("Cluj");
        addressEntity.setPostalCode("123456");
        userEntity.setAddress(addressEntity);

        // WHEN
        final UserEntity savedUser = userRepository.save(userEntity);

        // THEN
        // validate we can get the user from the database
        final Optional<UserEntity> loadedUser = userRepository.findById(savedUser.getId());
        assertTrue(loadedUser.isPresent());
        loadedUser.ifPresent(user ->
            assertThat(user).usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(userEntity)
        );
    }

    @Test
    void testUpdateSuccess() {
        // GIVEN
        final String id = "604107dde3835d7496be4e3d";
        final UserEntity user = userRepository.findById(id).get();
        user.setPassword("newPassword");

        // WHEN
        final UserEntity updatedUser = userRepository.save(user);

        // THEN
        assertThat(updatedUser).usingRecursiveComparison()
                .ignoringFields("password")
                .isEqualTo(user);
        assertEquals("newPassword", updatedUser.getPassword());
    }

    @Test
    void testDeleteSuccess() {
        // GIVEN
        final String id = "604107dde3835d7496be4e3d";

        // WHEN
        userRepository.deleteById(id);

        // THEN
        assertEquals(1, userRepository.findAll().size());
    }
}
