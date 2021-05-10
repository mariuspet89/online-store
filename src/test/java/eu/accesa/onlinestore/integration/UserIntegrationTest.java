package eu.accesa.onlinestore.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.dto.UserDtoNoId;
import eu.accesa.onlinestore.model.entity.UserEntity;
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

import static eu.accesa.onlinestore.utils.UserTestUtils.createUserDto;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MongoSpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration
@TestPropertySource(properties = {
        "JWT_ISSUER=eu.accesa.onlinestore",
        "JWT_SECRET=L6avrMtXQIhTP4tGc6qlz02RV46DCCEkQY25EOce3PAyJZkhD93ViPI44t9n2DP"
})
public class UserIntegrationTest {

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
    @DisplayName("GET //users/findAll - Found")
    @MongoDataFile(value = "UserData.json", classType = UserEntity.class, collectionName = "users")
    void testFindAll() throws Exception {
        ResultActions perform = mockMvc.perform(get("/users/findAll"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id").value(containsInAnyOrder("603648273ed85832b440eb99", "604107dde3835d7496be4e3d")))
                .andExpect(jsonPath("$[*].firstName").value(containsInAnyOrder("Lilah", "john")))
                .andExpect(jsonPath("$[*].lastName").value(containsInAnyOrder("Rozier", "travolta")))
                .andExpect(jsonPath("$[*].email").value(containsInAnyOrder("lrozier2@networksolutions.com", "johny@travolta.com")))
                .andExpect(jsonPath("$[*].username").value(containsInAnyOrder("lrozier2", "johnytravolta")))
                .andExpect(jsonPath("$[*].telephone").value(containsInAnyOrder("592-653-3873", "123456")))
                .andExpect(jsonPath("$[*].sex").value(containsInAnyOrder("F", "Mr")))
                .andExpect(jsonPath("$[*].password").value(containsInAnyOrder("$2y$12$pKggD4beeE8AJUTbLAu.7OwsBvtiHK7J2E/7fVTzLaKBh0XE/OThG", "johny123")))
                .andExpect(jsonPath("$[*].addressEntity.address").value(containsInAnyOrder("Frunzisului Street", "sunste blvd, nr 13")))
                .andExpect(jsonPath("$[*].addressEntity.city").value(containsInAnyOrder("Cluj-Napoca", "LA")))
                .andExpect(jsonPath("$[*].addressEntity.county").value(containsInAnyOrder("Cluj", "california")))
                .andExpect(jsonPath("$[*].addressEntity.postalCode").value(containsInAnyOrder("123456", "542545")));
    }

    @Test
    @WithMockUser
    @DisplayName("GET //users/findById - Found")
    @MongoDataFile(value = "UserData.json", classType = UserEntity.class, collectionName = "users")
    void testFindById() throws Exception {

        //Given
        String userId = "603648273ed85832b440eb99";
        //When Then
        mockMvc.perform(get("/users/{id}", userId))
                //Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(userId)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET //users/findByUsername - Found")
    @MongoDataFile(value = "UserData.json", classType = UserEntity.class, collectionName = "users")
    void testFindByUsername() throws Exception {
        //Given
        String username = "lrozier2";
        //When Then
        mockMvc.perform(get("/users/findByUsername", username)
                .param("username", username))
                //Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is(username)));
    }

    @Test
    @WithMockUser
    @DisplayName("GET //users/findByUsername - Found")
    @MongoDataFile(value = "UserData.json", classType = UserEntity.class, collectionName = "users")
    void testExistsByUsername() throws Exception {
        //Given
        String username = "lrozier2";
        //When
        mockMvc.perform(get("/users/existsByUsername")
                .param("username", username))
                //verify status and responce type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                //verify responce
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET //users/findByEmail - Found")
    @MongoDataFile(value = "UserData.json", classType = UserEntity.class, collectionName = "users")
    void testExistsByEmail() throws Exception {
        // GIVEN
        String email = "lrozier2@networksolutions.com";

        // WHEN, THEN
        mockMvc.perform(get("/users/existsByEmail")
                .param("email", email))
                // verify status and response type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // verify response
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET //users/findByEmail - Found")
    @MongoDataFile(value = "UserData.json", classType = UserEntity.class, collectionName = "users")
    void testFindByEmail() throws Exception {

        // GIVEN
        String email = "lrozier2@networksolutions.com";

        // WHEN, THEN
        mockMvc.perform(get("/users/findByEmail")
                .param("email", email))
                // verify status and response type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // verify response
                .andExpect(jsonPath("$.id").value("603648273ed85832b440eb99"))
                .andExpect(jsonPath("$.firstName").value("Lilah"))
                .andExpect(jsonPath("$.lastName").value("Rozier"))
                .andExpect(jsonPath("$.email").value("lrozier2@networksolutions.com"))
                .andExpect(jsonPath("$.username").value("lrozier2"))
                .andExpect(jsonPath("$.telephone").value("592-653-3873"))
                .andExpect(jsonPath("$.sex").value("F"))
                .andExpect(jsonPath("$.password").value("$2y$12$pKggD4beeE8AJUTbLAu.7OwsBvtiHK7J2E/7fVTzLaKBh0XE/OThG"))
                .andExpect(jsonPath("$.addressEntity.address").value("Frunzisului Street"))
                .andExpect(jsonPath("$.addressEntity.city").value("Cluj-Napoca"))
                .andExpect(jsonPath("$.addressEntity.county").value("Cluj"))
                .andExpect(jsonPath("$.addressEntity.postalCode").value("123456"));

    }

    @Test
    @WithMockUser
    @DisplayName("POST //users - User created")
    @MongoDataFile(value = "UserData.json", classType = UserEntity.class, collectionName = "users")
    void testCreateUser() throws Exception {
        //Setup mocked service
        UserDtoNoId requestDto = createUserDto(null, "Lilah1", "Rozier1", "lrozier2@networksolutions.com1",
                "lrozier21", "$2y$12$pKggD4beeE8AJUTbLAu.7OwsBvtiHK7J2E/7fVTzLaKBh0XE/OThG1", "592-653-38731", "M",
                "Frunzisului Street1", "Cluj-Napoca1", "Cluj1", "123451");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT //users - User updated")
    @MongoDataFile(value = "UserData.json", classType = UserEntity.class, collectionName = "users")
    void testUpdateUser() throws Exception {
        //Setup mocked service
        UserDtoNoId requestDto = createUserDto(null, "Lilah1", "Rozier1", "lrozier2@networksolutions.com1",
                "lrozier21", "test1", "592-653-38731", "M",
                "Frunzisului Street1", "Cluj-Napoca1", "Cluj1", "123451");
        String userId = "603648273ed85832b440eb99";

        mockMvc.perform(put("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDto)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(jsonPath("$.firstName", is("Lilah1")))
                .andExpect(jsonPath("$.lastName", is("Rozier1")))
                .andExpect(jsonPath("$.email", is("lrozier2@networksolutions.com1")))
                .andExpect(jsonPath("$.username", is("lrozier21")))
                .andExpect(jsonPath("$.telephone", is("592-653-38731")))
                .andExpect(jsonPath("$.sex", is("M")))
                .andExpect(jsonPath("$.addressEntity.address", is("Frunzisului Street1")))
                .andExpect(jsonPath("$.addressEntity.city", is("Cluj-Napoca1")))
                .andExpect(jsonPath("$.addressEntity.county", is("Cluj1")))
                .andExpect(jsonPath("$.addressEntity.postalCode", is("123451")));

    }

    @Test
    @WithMockUser
    @DisplayName("PUT //users - User deleted")
    @MongoDataFile(value = "UserData.json", classType = UserEntity.class, collectionName = "users")
    void testDeleteUser() throws Exception {
        //GIVEN
        String userId = "603648273ed85832b440eb99";

        //WHEN
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")));
    }


    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
