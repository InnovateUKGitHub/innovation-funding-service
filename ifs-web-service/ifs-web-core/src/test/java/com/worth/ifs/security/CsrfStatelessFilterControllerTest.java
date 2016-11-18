package com.worth.ifs.security;

import com.worth.ifs.commons.security.authentication.user.UserAuthentication;
import com.worth.ifs.config.IfSThymeleafDialect;
import com.worth.ifs.user.resource.UserResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * <p>
 * CSRF stateless filter test using a sample controller.
 * Performs requests with a combination of different request methods and tokens, checking the expected responses.
 * </p>
 * <p>
 * Also checks that a sample Thymeleaf view template with a form (csrf-test.html) returned by the controller has the appropriate CSRF hidden field injected into it.
 * </p>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CsrfStatelessFilterControllerTest.ContextConfiguration.class)
@TestPropertySource(properties = {"ifs.web.security.csrf.encryption.password = a180fb6c-878a-4850-bccc-bd244f4c41c9", "ifs.web.security.csrf.encryption.salt: 9ea751556a3feee7", "ifs.web.security.csrf.token.validity.mins: 30"})
@WebAppConfiguration
@ActiveProfiles("CsrfStatelessFilterControllerTest")
public class CsrfStatelessFilterControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CsrfStatelessFilter csrfStatelessFilter;

    private MockMvc mockMvc;
    private final TextEncryptor encryptor = Encryptors.text(ENCRYPTION_PASSWORD, ENCRYPTION_SALT);

    private static final String ENCRYPTION_PASSWORD = "a180fb6c-878a-4850-bccc-bd244f4c41c9";
    private static final String ENCRYPTION_SALT = "9ea751556a3feee7";
    private static final String UID = "5cc0ac0d-b969-40f5-9cc5-b9bdd98c86de";

    @Before
    public void setUp() {
        final UserResource user = newUserResource().withId(-1L).withUID(UID).build();
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(csrfStatelessFilter)
                .build();
    }

    @After
    public void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void test_checkCsrfTokenIsGeneratedInResponse() throws Exception {
        // test that a csrf token is present in the form body, and also in a cookie to be picked up by Ajax requests
        final MvcResult result = mockMvc.perform(get("/csrf-test/test-get"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("csrf-test"))
                .andExpect(cookie().exists("CSRF-TOKEN"))
                .andReturn();

        // test that the view which contains a form has had a hidden field with the CSRF token injected into it
        final String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("<input type=\"hidden\" name=\"_csrf\""));
    }

    @Test
    public void test_unprotectedRequest() throws Exception {
        mockMvc.perform(get("/csrf-test/test-get"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("csrf-test"))
                .andReturn();
    }

    @Test
    public void test_protectedRequest_tokenMissing() throws Exception {
        mockMvc.perform(post("/csrf-test/test-post"))
                .andExpect(status().is(FORBIDDEN.value()))
                .andReturn();
    }

    @Test
    public void test_protectedRequest_byHeader_empty() throws Exception {
        mockMvc.perform(post("/csrf-test/test-post")
                .header("X-CSRF-TOKEN", EMPTY))
                .andExpect(status().is(FORBIDDEN.value()))
                .andReturn();
    }

    @Test
    public void test_protectedRequest_byHeader_malformed() throws Exception {
        mockMvc.perform(post("/csrf-test/test-post")
                .header("X-CSRF-TOKEN", malformedToken()))
                .andExpect(status().is(FORBIDDEN.value()))
                .andReturn();
    }

    @Test
    public void test_protectedRequest_byHeader() throws Exception {
        mockMvc.perform(post("/csrf-test/test-post")
                .header("X-CSRF-TOKEN", validToken()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("csrf-test"))
                .andReturn();
    }

    @Test
    public void test_protectedRequest_byParameter_empty() throws Exception {
        mockMvc.perform(post("/csrf-test/test-post")
                .param("_csrf", EMPTY))
                .andExpect(status().is(FORBIDDEN.value()))
                .andReturn();
    }

    @Test
    public void test_protectedRequest_byParameter_malformed() throws Exception {
        mockMvc.perform(post("/csrf-test/test-post")
                .param("_csrf", malformedToken()))
                .andExpect(status().is(FORBIDDEN.value()))
                .andReturn();
    }

    @Test
    public void test_protectedRequest_byParameter() throws Exception {
        mockMvc.perform(post("/csrf-test/test-post")
                .param("_csrf", validToken()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("csrf-test"))
                .andReturn();
    }

    private String encrypt(final CsrfTokenService.CsrfUidToken token) {
        return encryptor.encrypt(token.getToken());
    }

    private String token(final String uid, final Instant timestamp) {
        return encrypt(new CsrfTokenService.CsrfUidToken(randomUUID().toString(), uid, timestamp));
    }


    private String validToken() {
        return token(UID, Instant.now());
    }

    private String malformedToken() {
        return validToken().substring(1);
    }

    @Controller
    @RequestMapping("/csrf-test")
    static class SampleController {

        @RequestMapping(value = "/test-post", method = POST)
        public String testPost() {
            return "csrf-test";
        }


        @RequestMapping(value = "/test-get", method = GET)
        public String testGet() {
            return "csrf-test";
        }
    }

    /**
     * <p>
     * Spring Configuration class.<br>
     * Using a unique @Profile to make sure that all of the @Bean methods are bypassed unless the profile is active,
     * which will only be the case for this integration test.
     * Without this restriction, these beans clash with the real ones during the running of
     * other {@link com.worth.ifs.BaseWebIntegrationTest} integration tests which load the main Spring application configuration as well as this one.
     * </p>
     */
    @Configuration
    @EnableWebSecurity
    @EnableWebMvc
    @Profile("CsrfStatelessFilterControllerTest")
    static class ContextConfiguration {

        @Bean
        public CsrfTokenService tokenUtility() {
            return new CsrfTokenService();
        }

        @Bean
        public CsrfStatelessFilter csrfStatelessFilter() {
            return new CsrfStatelessFilter();
        }

        @Bean
        public SampleController sampleController() {
            return new SampleController();
        }

        @Bean
        public PropertySourcesPlaceholderConfigurer propertiesResolver() {
            return new PropertySourcesPlaceholderConfigurer();
        }

        @Bean
        public ViewResolver viewResolver() {
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setTemplateMode("HTML5");
            templateResolver.setSuffix(".html");
            templateResolver.setPrefix("templates/");

            final Set<IDialect> additionalDialects = new HashSet<>();
            additionalDialects.add(new IfSThymeleafDialect());

            SpringTemplateEngine engine = new SpringTemplateEngine();
            engine.setTemplateResolver(templateResolver);
            engine.setAdditionalDialects(additionalDialects);

            ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
            viewResolver.setTemplateEngine(engine);
            return viewResolver;
        }
    }
}
