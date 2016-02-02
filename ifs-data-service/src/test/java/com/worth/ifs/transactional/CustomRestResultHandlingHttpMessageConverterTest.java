package com.worth.ifs.transactional;

import com.worth.ifs.BaseWebIntegrationTest;
import com.worth.ifs.application.resource.InviteCollaboratorResource;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static com.worth.ifs.commons.security.TokenAuthenticationService.AUTH_TOKEN;
import static com.worth.ifs.commons.service.BaseRestService.getJSONHeaders;
import static org.springframework.http.HttpMethod.POST;

/**
 *
 */
@Ignore("TODO DW - INFUND-854 - move test to point at different url")
public class CustomRestResultHandlingHttpMessageConverterTest extends BaseWebIntegrationTest {

    @Value("${ifs.data.service.rest.baseURL}")
    private String dataUrl;

    @Test
    // TODO DW - INFUND-854 - implement test properly
    public void testasdf() {

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(dataUrl + "/application/1/invitecollaborator", POST, jsonEntity(new InviteCollaboratorResource("Recipient", "email@example.com", "http://localhost")), String.class);
            System.out.println(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println(e.getResponseBodyAsString());
        }
    }

    protected <T> HttpEntity<T> jsonEntity(T entity){
        HttpHeaders headers = getJSONHeaders();
        headers.set(AUTH_TOKEN, "123abc");
        return new HttpEntity<>(entity, headers);
    }
}
