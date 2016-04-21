package com.worth.ifs.security;

import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.user.resource.UserResource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TestPropertySource(properties = { "ifs.web.security.csrf.encryption.password = a180fb6c-878a-4850-bccc-bd244f4c41c9", "ifs.web.security.csrf.encryption.salt: 9ea751556a3feee7" })
@WebAppConfiguration
public class CsrfStatelessFilterControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CsrfTokenUtility tokenUtility;

    @Autowired
    private CsrfStatelessFilter csrfStatelessFilter;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        final UserResource user = newUserResource().withId(-1L).withUID("5cc0ac0d-b969-40f5-9cc5-b9bdd98c86de").build();
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
    public void test_csrfTokenGenerated() throws Exception {
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
    public void test_protectedRequest_byHeader() throws Exception {
        final String token = tokenUtility.generateToken().getToken();

        mockMvc.perform(post("/csrf-test/test-post").header("X-CSRF-TOKEN", token))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("csrf-test"))
                .andReturn();
    }

    @Test
    public void test_protectedRequest_byParameter() throws Exception {
        final String token = tokenUtility.generateToken().getToken();

        mockMvc.perform(post("/csrf-test/test-post").param("_csrf", token))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("csrf-test"))
                .andReturn();
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

    @Configuration
    @EnableWebSecurity
    @EnableWebMvc
    static class ContextConfiguration {

        @Bean
        public CsrfTokenUtility tokenUtility() {
            return new CsrfTokenUtility();
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
        public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
            return new PropertySourcesPlaceholderConfigurer();
        }

        @Bean
        public ViewResolver viewResolver() {
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setTemplateMode("HTML5");
            templateResolver.setSuffix(".html");
            templateResolver.setPrefix("templates/");

            SpringTemplateEngine engine = new SpringTemplateEngine();
            engine.setTemplateResolver(templateResolver);

            ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
            viewResolver.setTemplateEngine(engine);
            return viewResolver;
        }
    }
}
