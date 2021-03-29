package eu.accesa.onlinestore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.dto.UserDto;
import eu.accesa.onlinestore.model.dto.UserDtoNoId;
import eu.accesa.onlinestore.model.entity.AddressEntity;
import eu.accesa.onlinestore.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static eu.accesa.onlinestore.utils.UserTestUtils.createUserDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {UserController.class})
class UserControllerTest {

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<UserDtoNoId> userDtoNoIdArgumentCaptor;

    @Test
    void testFindAll() throws Exception {
        // GIVEN
        UserDto mockUser1 = createUserDto("1", "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Las Vegas", "Nevada", "123456");
        UserDto mockUser2 = createUserDto("2", "John", "Travolta", "johntravolta@movies.com",
                "johntravolta", "guns", "123-456-789-0", "M", "Main Street 2",
                "Los Angeles", "California", "123457");

        doReturn(List.of(mockUser1, mockUser2)).when(userService).findAll();

        // WHEN
        mockMvc.perform(get("/users/findAll"))
                // validate the status and response content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // validate the response
                .andExpect(jsonPath("$[*].id")
                        .value(containsInAnyOrder("1", "2")))
                .andExpect(jsonPath("$[*].firstName")
                        .value(containsInAnyOrder("John", "John")))
                .andExpect(jsonPath("$[*].lastName")
                        .value(containsInAnyOrder("Wayne", "Travolta")))
                .andExpect(jsonPath("$[*].email")
                        .value(containsInAnyOrder("johnwayne@movies.com", "johntravolta@movies.com")))
                .andExpect(jsonPath("$[*].username")
                        .value(containsInAnyOrder("johnwayne", "johntravolta")))
                .andExpect(jsonPath("$[*].password")
                        .value(containsInAnyOrder("pistols", "guns")))
                .andExpect(jsonPath("$[*].telephone")
                        .value(containsInAnyOrder("123-456-789", "123-456-789-0")))
                .andExpect(jsonPath("$[*].sex")
                        .value(containsInAnyOrder("M", "M")))
                .andExpect(jsonPath("$[*].addressEntity.address")
                        .value(containsInAnyOrder("Main Street 1", "Main Street 2")))
                .andExpect(jsonPath("$[*].addressEntity.city")
                        .value(containsInAnyOrder("Las Vegas", "Los Angeles")))
                .andExpect(jsonPath("$[*].addressEntity.county")
                        .value(containsInAnyOrder("Nevada", "California")))
                .andExpect(jsonPath("$[*].addressEntity.postalCode")
                        .value(containsInAnyOrder("123456", "123457")));

        // THEN
        verify(userService).findAll();
        verifyNoMoreInteractions(userService);
    }

    @Test
    void testFindById() throws Exception {
        // GIVEN
        UserDto mockUser = createUserDto("1", "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Las Vegas", "Nevada", "123456");
        String id = mockUser.getId();

        doReturn(mockUser).when(userService).findById(anyString());

        // WHEN
        mockMvc.perform(get("/users/{id}", id))
                // validate the status and response content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // validate response
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Wayne")))
                .andExpect(jsonPath("$.email", is("johnwayne@movies.com")))
                .andExpect(jsonPath("$.username", is("johnwayne")))
                .andExpect(jsonPath("$.password", is("pistols")))
                .andExpect(jsonPath("$.telephone", is("123-456-789")))
                .andExpect(jsonPath("$.sex", is("M")))
                .andExpect(jsonPath("$.addressEntity.address", is("Main Street 1")))
                .andExpect(jsonPath("$.addressEntity.city", is("Las Vegas")))
                .andExpect(jsonPath("$.addressEntity.county", is("Nevada")))
                .andExpect(jsonPath("$.addressEntity.postalCode", is("123456")));

        // THEN
        verify(userService).findById(id);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void testExistsByUsername() throws Exception {
        // GIVEN
        String username = "johnwayne";

        doReturn(true).when(userService).existsByUsername(username);

        // WHEN, THEN
        mockMvc.perform(get("/users/existsByUsername")
                .param("username", username))
                // verify status and response type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // verify response
                .andExpect(content().string("true"));
    }

    @Test
    void testFindByUsername() throws Exception {
        // GIVEN
        UserDto mockUser = createUserDto("1", "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Las Vegas", "Nevada", "123456");
        AddressEntity address = mockUser.getAddressEntity();
        String username = mockUser.getUsername();

        doReturn(mockUser).when(userService).findByUsername(username);

        // WHEN
        mockMvc.perform(get("/users/findByUsername")
                .param("username", username))
                // validate the status and response content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // validate response
                .andExpect(jsonPath("$.id", is(mockUser.getId())))
                .andExpect(jsonPath("$.firstName", is(mockUser.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(mockUser.getLastName())))
                .andExpect(jsonPath("$.email", is(mockUser.getEmail())))
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.password", is(mockUser.getPassword())))
                .andExpect(jsonPath("$.telephone", is(mockUser.getTelephone())))
                .andExpect(jsonPath("$.sex", is(mockUser.getSex())))
                .andExpect(jsonPath("$.addressEntity.address", is(address.getAddress())))
                .andExpect(jsonPath("$.addressEntity.city", is(address.getCity())))
                .andExpect(jsonPath("$.addressEntity.county", is(address.getCounty())))
                .andExpect(jsonPath("$.addressEntity.postalCode", is(address.getPostalCode())));

        // THEN
        verify(userService).findByUsername(username);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void testExistsByEmail() throws Exception {
        // GIVEN
        String email = "johnwayne@email.com";

        doReturn(true).when(userService).existsByEmail(email);

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
    void testFindByEmail() throws Exception {
        // GIVEN
        UserDto mockUser = createUserDto("1", "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Las Vegas", "Nevada", "123456");
        AddressEntity address = mockUser.getAddressEntity();
        String email = mockUser.getEmail();

        doReturn(mockUser).when(userService).findByEmail(email);

        // WHEN
        mockMvc.perform(get("/users/findByEmail")
                .param("email", email))
                // validate the status and response content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // validate response
                .andExpect(jsonPath("$.id", is(mockUser.getId())))
                .andExpect(jsonPath("$.firstName", is(mockUser.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(mockUser.getLastName())))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.username", is(mockUser.getUsername())))
                .andExpect(jsonPath("$.password", is(mockUser.getPassword())))
                .andExpect(jsonPath("$.telephone", is(mockUser.getTelephone())))
                .andExpect(jsonPath("$.sex", is(mockUser.getSex())))
                .andExpect(jsonPath("$.addressEntity.address", is(address.getAddress())))
                .andExpect(jsonPath("$.addressEntity.city", is(address.getCity())))
                .andExpect(jsonPath("$.addressEntity.county", is(address.getCounty())))
                .andExpect(jsonPath("$.addressEntity.postalCode", is(address.getPostalCode())));

        // THEN
        verify(userService).findByEmail(email);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void testCreateUser() throws Exception {
        // GIVEN
        UserDtoNoId requestDto = createUserDto(null, "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Las Vegas", "Nevada", "123456");

        AddressEntity addressEntity = requestDto.getAddressEntity();
        UserDto createdUser = createUserDto("1", requestDto.getFirstName(), requestDto.getLastName(), requestDto.getEmail(),
                requestDto.getUsername(), requestDto.getPassword(), requestDto.getTelephone(), requestDto.getSex(),
                addressEntity.getAddress(), addressEntity.getCity(), addressEntity.getCounty(), addressEntity.getPostalCode());

        doReturn(createdUser).when(userService).createUser(userDtoNoIdArgumentCaptor.capture());

        // WHEN
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDto)))
                // validate the status and response content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // validate response
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Wayne")))
                .andExpect(jsonPath("$.email", is("johnwayne@movies.com")))
                .andExpect(jsonPath("$.username", is("johnwayne")))
                .andExpect(jsonPath("$.password", is("pistols")))
                .andExpect(jsonPath("$.telephone", is("123-456-789")))
                .andExpect(jsonPath("$.sex", is("M")))
                .andExpect(jsonPath("$.addressEntity.address", is("Main Street 1")))
                .andExpect(jsonPath("$.addressEntity.city", is("Las Vegas")))
                .andExpect(jsonPath("$.addressEntity.county", is("Nevada")))
                .andExpect(jsonPath("$.addressEntity.postalCode", is("123456")));

        // THEN
        verify(userService).createUser(any(UserDtoNoId.class));
        verifyNoMoreInteractions(userService);

        // verify the request DTO has the same data as the response DTO
        assertThat(userDtoNoIdArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(requestDto);
    }

    @Test
    void testUpdateUser() throws Exception {
        // GIVEN
        String userId = "1";
        String newPostalCode = "654321";
        UserDtoNoId requestDto = createUserDto(null, "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Las Vegas", "Nevada", newPostalCode);
        UserDto updatedUserDto = createUserDto(userId, "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Las Vegas", "Nevada", newPostalCode);

        doReturn(updatedUserDto).when(userService).updateUser(eq(userId), userDtoNoIdArgumentCaptor.capture());

        // WHEN
        mockMvc.perform(put("/users/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDto)))
                // validate the status and response content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // validate updated fields
                .andExpect(jsonPath("$.id", is(userId)))
                .andExpect(jsonPath("$.addressEntity.postalCode", is(newPostalCode)));

        // THEN
        verify(userService).updateUser(anyString(), any(UserDtoNoId.class));
        verifyNoMoreInteractions(userService);

        // verify the request DTO with updated postal code corresponds to the response DTO with updated postalCode
        assertThat(userDtoNoIdArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(updatedUserDto);
    }

    @Test
    void testDeleteUser() throws Exception {
        // GIVEN
        String userId = "1";

        // WHEN
        mockMvc.perform(delete("/users/{id}", userId))
                // validate the status and response content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                // validate body response
                .andExpect(content().string(containsString("User Deleted")))
                .andReturn().getResponse().getContentAsString();

        // THEN
        verify(userService).deleteUser(userId);
        verifyNoMoreInteractions(userService);
    }

    private String asJsonString(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
