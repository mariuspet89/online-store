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
        assertThat(users).as("findAll() should have returned 2 users!").hasSize(2);
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

            final AddressEntity addressEntity = user.getAddressEntity();
            assertNotNull(addressEntity);
            assertEquals("sunste blvd, nr 13", addressEntity.getAddress());
            assertEquals("LA", addressEntity.getCity());
            assertEquals("california", addressEntity.getCounty());
            assertEquals("542545", addressEntity.getPostalCode());

            assertFalse(user.isEnabled());
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
    void existsByUsernameSuccess() {
        // GIVEN
        final String username = "johnytravolta";

        // WHEN
        boolean existsByUsername = userRepository.existsByUsername(username);

        // THEN
        assertTrue(existsByUsername);
    }

    @Test
    void existsByUsernameFailure() {
        // GIVEN
        final String username = "fakeUsername";

        // WHEN
        boolean existsByUsername = userRepository.existsByUsername(username);

        // THEN
        assertFalse(existsByUsername);
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

            final AddressEntity addressEntity = user.getAddressEntity();
            assertNotNull(addressEntity);
            assertEquals("sunste blvd, nr 13", addressEntity.getAddress());
            assertEquals("LA", addressEntity.getCity());
            assertEquals("california", addressEntity.getCounty());
            assertEquals("542545", addressEntity.getPostalCode());

            assertFalse(user.isEnabled());
        });
    }

    @Test
    void testFindByUsernameFailure() {
        // GIVEN
        final String username = "fakeUsername";

        // WHEN
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);

        // THEN
        assertFalse(userEntityOptional.isPresent(), "A user with username = " + username + " should not be present!");
    }

    @Test
    void existsByEmailSuccess() {
        // GIVEN
        final String email = "lrozier2@networksolutions.com";

        // WHEN
        boolean existsByEmail = userRepository.existsByEmail(email);

        // THEN
        assertTrue(existsByEmail);
    }

    @Test
    void existsByEmailFailure() {
        // GIVEN
        final String email = "fakeEmail";

        // WHEN
        boolean existsByEmail = userRepository.existsByEmail(email);

        // THEN
        assertFalse(existsByEmail);
    }

    @Test
    void testFindByEmailSuccess() {
        // GIVEN
        final String email = "lrozier2@networksolutions.com";

        // WHEN
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);

        // THEN
        assertTrue(userEntityOptional.isPresent(), "A user with email = " + email + " should be present!");
        userEntityOptional.ifPresent(user -> {
            assertEquals("603648273ed85832b440eb99", user.getId());
            assertEquals("Lilah", user.getFirstName());
            assertEquals("Rozier", user.getLastName());
            assertEquals(email, user.getEmail());
            assertEquals("lrozier2", user.getUsername());
            assertEquals("592-653-3873", user.getTelephone());
            assertEquals("F", user.getSex());
            assertEquals("$2y$12$pKggD4beeE8AJUTbLAu.7OwsBvtiHK7J2E/7fVTzLaKBh0XE/OThG", user.getPassword());

            final AddressEntity addressEntity = user.getAddressEntity();
            assertNotNull(addressEntity);
            assertEquals("Frunzisului Street", addressEntity.getAddress());
            assertEquals("Cluj-Napoca", addressEntity.getCity());
            assertEquals("Cluj", addressEntity.getCounty());
            assertEquals("123456", addressEntity.getPostalCode());

            assertTrue(user.isEnabled());
        });
    }

    @Test
    void testFindByEmailFailure() {
        // GIVEN
        final String email = "fakeEmail";

        // WHEN
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);

        // THEN
        assertFalse(userEntityOptional.isPresent(), "A user with email = " + email + " should not be present!");
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
        userEntity.setAddressEntity(addressEntity);

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

        // THEN`
        assertThat(userRepository.findAll()).hasSize(1);
    }
}
