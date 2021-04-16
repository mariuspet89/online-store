package eu.accesa.onlinestore.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(apiKey()))
                .globalRequestParameters(headers())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("Online-store API",
                "Some custom description of API.",
                "1.0",
                "Terms of service",
                new Contact("Some one from Accesa", "www.accesa.eu", "dummy@ader.eu"),
                "License of API",
                "API license URL",
                Collections.emptyList());
    }

    private HttpAuthenticationScheme apiKey() {
        return HttpAuthenticationScheme.JWT_BEARER_BUILDER.name("JWT").build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = new AuthorizationScope("global", "accessEverything");
        return List.of(new SecurityReference("JWT", authorizationScopes));
    }

    private List<RequestParameter> headers() {
        return Collections.singletonList(new RequestParameterBuilder()
                .name(HttpHeaders.ACCEPT_LANGUAGE)
                .in(ParameterType.HEADER)
                .build());
    }
}
