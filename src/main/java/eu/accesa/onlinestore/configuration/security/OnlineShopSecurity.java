package eu.accesa.onlinestore.configuration.security;

import eu.accesa.onlinestore.configuration.security.handler.AuthenticationSuccessHandlerImpl;
import eu.accesa.onlinestore.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Configuration
public class OnlineShopSecurity extends WebSecurityConfigurerAdapter {

    private final AuthenticationSuccessHandlerImpl authenticationSuccessHandler;
    private final UserRepository userRepository;
    private final JwtTokenFilter jwtTokenFilter;

    public OnlineShopSecurity(AuthenticationSuccessHandlerImpl authenticationSuccessHandler,
                              UserRepository userRepository, JwtTokenFilter jwtTokenFilter) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.userRepository = userRepository;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Enable CORS and disable CSRF
        http = http.cors().and().csrf().disable();

        // Set session management to stateless
        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();

        // Set unauthorized requests exception handler
        http = http
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, authException) -> response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                authException.getMessage()))
                .and();

        final String[] swaggerAuthWhitelist = {
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v3/api-docs",
        };

        // Set permissions on endpoints
        http.authorizeRequests()
                // public endpoints (e.g. Swagger)
                .mvcMatchers("/login/**").permitAll()
                .mvcMatchers(swaggerAuthWhitelist).permitAll()
                .mvcMatchers(HttpMethod.GET, "/products/**", "/users/reset-password").permitAll()
                .mvcMatchers(HttpMethod.GET, "/users/existsByUsername", "/users/existsByEmail").permitAll()
                .mvcMatchers(HttpMethod.POST, "/users", "/users/forgot-password").permitAll()
                .mvcMatchers(HttpMethod.PUT, "/userConfirmation", "/users/**").permitAll()
                // private endpoints
                .anyRequest().authenticated();

        http.oauth2Login()
                .successHandler(authenticationSuccessHandler);

        // Add JWT token filter (in the filter chain)
        http.addFilterAt(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(username -> userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Username %s was not found!", username))
                )
        ).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://18.224.7.25:5000");
        config.addAllowedHeader("*");
        config.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setExposedHeaders(List.of("Access-Control-Expose-Headers", "Authorization", "Cache-Control",
                "Content-Type", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "Origin"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
