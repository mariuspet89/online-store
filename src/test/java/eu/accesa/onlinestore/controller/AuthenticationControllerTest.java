package eu.accesa.onlinestore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.configuration.security.JwtTokenUtil;
import eu.accesa.onlinestore.model.dto.AuthRequestDto;
import eu.accesa.onlinestore.model.entity.UserEntity;
import eu.accesa.onlinestore.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static eu.accesa.onlinestore.utils.UserTestUtils.createUserEntity;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {AuthenticationController.class})
class AuthenticationControllerTest {

    private static final String JWT_TEST_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI2MDRmMTRlMmY4Y2I2YTFiYjQ0YzZjYmEsdGVzdCIsImlzcyI6ImV1LmFjY2VzYS5vbmxpbmVzdG9yZSIsImlhdCI6MTYxNTgxNTQyMSwiZXhwIjoxNjE2NDIwMjIxfQ.B0yg2RkeF3jxAU2qEe7kbXEGfKbYmsyVBQ174-IXeAwEgUfThQA5s8HMdTLmB-pBoGuWJ_zbStUi6B78tAMJiw";

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    UserServiceImpl userService;

    @SpyBean
    private ModelMapper modelMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testLoginSuccess() throws Exception {
        // GIVEN
        String username = "johnwayne";
        String password = "test";

        // request DTO
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername(username);
        requestDto.setPassword(password);

        // authentication mock
        UserEntity userEntity = createUserEntity("1", "John", "Wayne", "johnwayne@movies.com",
                username, password, "123-456-789", "M", "Main Street 1",
                "Main Street 1", "Nevada", "123456");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userEntity);
        when(jwtTokenUtil.generateAccessToken(any(UserEntity.class))).thenReturn(JWT_TEST_TOKEN);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                // prepare request
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDto)))
                // validate status, content type and authorization response header
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, JWT_TEST_TOKEN))
                // validate response DTO
                .andExpect(jsonPath("$.id", is(userEntity.getId())))
                .andExpect(jsonPath("$.firstName", is(userEntity.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(userEntity.getLastName())))
                .andExpect(jsonPath("$.email", is(userEntity.getEmail())))
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.password", is(password)))
                .andExpect(jsonPath("$.telephone", is(userEntity.getTelephone())))
                .andExpect(jsonPath("$.sex", is(userEntity.getSex())))
                .andExpect(jsonPath("$.addressEntity.address", is(userEntity.getAddressEntity().getAddress())))
                .andExpect(jsonPath("$.addressEntity.city", is(userEntity.getAddressEntity().getCity())))
                .andExpect(jsonPath("$.addressEntity.county", is(userEntity.getAddressEntity().getCounty())))
                .andExpect(jsonPath("$.addressEntity.postalCode", is(userEntity.getAddressEntity().getPostalCode())));
    }

    @Test
    void testLoginFailure() throws Exception {
        // GIVEN
        // request DTO
        AuthRequestDto requestDto = new AuthRequestDto();
        requestDto.setUsername("incorrectUser");
        requestDto.setPassword("incorrectPassword");

        // authentication mock
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(
                new BadCredentialsException("Bad credentials"));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                // prepare request
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDto)))
                // validate status
                .andExpect(status().isUnauthorized());
    }
    @Test
    void userConfirmation() throws Exception {
        //Given
        String userId="userId";
        String username = "johnwayne";
        String password = "test";
        UserEntity user=createUserEntity("userId","John", "Wayne", "johnwayne@movies.com",
                username, password, "123-456-789", "M", "Main Street 1",
                "Main Street 1", "Nevada", "123456");
        user.setEnabled(true);
        String message="Your account is confirmed!";

        when(userService.confirmUser(userId)).thenReturn(message);

        final ObjectMapper objectMapper = new ObjectMapper();
        final String userJson = objectMapper.writeValueAsString(user);
        final ResultActions resultActions = mockMvc.perform(put("/userConfirmation")
                .param("userId",userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson));
        resultActions.andExpect(status().isOk())
                .andExpect(content().string("Your account is confirmed!"));
    }

    private String asJsonString(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
