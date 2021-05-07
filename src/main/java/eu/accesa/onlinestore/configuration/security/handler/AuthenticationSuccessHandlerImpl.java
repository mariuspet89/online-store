package eu.accesa.onlinestore.configuration.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.configuration.security.JwtTokenUtil;
import eu.accesa.onlinestore.model.dto.UserDto;
import eu.accesa.onlinestore.model.entity.UserEntity;
import eu.accesa.onlinestore.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public AuthenticationSuccessHandlerImpl(JwtTokenUtil jwtTokenUtil, ModelMapper modelMapper,
                                            ObjectMapper objectMapper, UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
            final OAuth2User principal = oAuth2Token.getPrincipal();
            UserEntity userEntity = null;
            if (oAuth2Token.getAuthorizedClientRegistrationId().equals("facebook")) {
                final Optional<UserEntity> optionalUser = userRepository.findByEmail(principal.getAttribute("email"));
                userEntity = optionalUser.orElseGet(() -> createUserEntityFromOAuth2User(principal));
            } else if (principal instanceof OidcUser) {
                OidcUser oidcUser = (OidcUser) principal;
                final Optional<UserEntity> optionalUser = userRepository.findByEmail(oidcUser.getEmail());
                userEntity = optionalUser.orElseGet(() -> createUserEntityFromOidcUser(oidcUser));
            }

            // set JWT token on response
            final String jwtToken = jwtTokenUtil.generateAccessToken(userEntity);
            response.setHeader(HttpHeaders.AUTHORIZATION, jwtToken);

            // convert userEntity to JSON
            final UserDto userDto = modelMapper.map(userEntity, UserDto.class);
            final String jsonResponse = objectMapper.writeValueAsString(userDto);

            // set response content type and encoding
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            // set the JSON response body
            final PrintWriter writer = response.getWriter();
            writer.write(jsonResponse);
            writer.flush();
        }
    }

    private UserEntity createUserEntityFromOAuth2User(OAuth2User oAuth2User) {
        final UserEntity userEntity = new UserEntity();
        final String fullName = oAuth2User.getAttribute("name");
        userEntity.setFirstName(fullName.split(" ")[0]);
        userEntity.setLastName(fullName.split(" ")[1]);
        userEntity.setEmail(oAuth2User.getAttribute("email"));
        userEntity.setUsername(oAuth2User.getName());
        userEntity.setEnabled(true);

        userRepository.save(userEntity);
        return userEntity;
    }

    private UserEntity createUserEntityFromOidcUser(OidcUser oidcUser) {
        final UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(oidcUser.getGivenName());
        userEntity.setLastName(oidcUser.getFamilyName());
        userEntity.setEmail(oidcUser.getEmail());
        userEntity.setUsername(oidcUser.getSubject());

        userRepository.save(userEntity);
        return userEntity;
    }
}
