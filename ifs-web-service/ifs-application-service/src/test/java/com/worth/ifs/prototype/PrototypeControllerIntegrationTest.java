package com.worth.ifs.prototype;

import com.worth.ifs.BaseWebIntegrationTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

/**
 * Integration test of the Prototype Controller with the "prototypes" profile to check that requests are found when they should be.
 */
@Ignore("Ignored since fetching these pages invokes com.worth.ifs.interceptors.AlertMessageHandlerInterceptor.addAlertMessages and the data controllers are not deployed to the embedded Tomcat")
@ActiveProfiles("prototypes")
public class PrototypeControllerIntegrationTest extends BaseWebIntegrationTest {

    @Value("http://localhost:${local.server.port}")
    private String baseWebUrl;

    private RestTemplate template = new TestRestTemplate();

    private static final String KNOWN_PROTOTYPE = "/prototypes/631-finances-assigned-to-you";

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test_getPrototypeIndex() throws Exception {
        ResponseEntity<String> entity = template.getForEntity(baseWebUrl + "/prototypes", String.class);
        String body = entity.getBody();
        assertEquals(OK, entity.getStatusCode());
        assertEquals(TEXT_HTML_VALUE + ";charset=UTF-8", entity.getHeaders().getContentType().toString());
        assertTrue(body.contains("<h2 class=\"subtitle\">Directory of front end code to show user journeys</h2>"));
    }

    @Test
    public void test_getPrototypePage() throws Exception {
        ResponseEntity<String> entity = template.getForEntity(baseWebUrl + KNOWN_PROTOTYPE, String.class);
        String body = entity.getBody();
        assertEquals(OK, entity.getStatusCode());
        assertEquals(TEXT_HTML_VALUE + ";charset=UTF-8", entity.getHeaders().getContentType().toString());
        assertTrue(body.contains("Empire Ltd Finances"));
    }

    @Test
    @Ignore("fault with the controller not testing if the requested Thymeleaf template exists")
    public void test_getPrototypePage_noTemplateResource() throws Exception {
        ResponseEntity<String> entity = template.getForEntity(baseWebUrl + "/prototypes/template-does-not-exist", String.class);
        assertEquals(NOT_FOUND, entity.getStatusCode());
    }

}