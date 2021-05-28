package org.innovateuk.ifs.config.swagger;

import io.swagger.annotations.ApiOperation;
import org.innovateuk.ifs.commons.rest.RestFailure;
import org.innovateuk.ifs.commons.security.authentication.token.Authentication;
import org.innovateuk.ifs.security.config.SecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

import static springfox.documentation.builders.RequestHandlerSelectors.withMethodAnnotation;

/**
 * Setup swagger for testing and debugging.
 *
 * This extends existing security and enables additional whitelists for swagger endpoints.
 *
 * Additionally downstream calls are secured with the auth token scheme already in use.
 */
@Configuration
@EnableSwagger2
@Profile("swagger")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SwaggerConfig extends SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    public SwaggerConfig() {
        super(AUTH_WHITELIST, true);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                // Secure everything
                .securityContexts(Collections.singletonList(securityContext()))
                // And use the header token set-up as per the rest of the project
                .securitySchemes(Collections.singletonList(apiKey()))
                .select()
                // only use swagger-ui with these method annotations
                .apis(withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                // Ignore these models
                .build().ignoredParameterTypes(
                        Error.class,
                        RestFailure.class
                );
    }

    private ApiKey apiKey() {
        return new ApiKey(Authentication.TOKEN, Authentication.TOKEN, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference(Authentication.TOKEN, authorizationScopes));
    }

}
