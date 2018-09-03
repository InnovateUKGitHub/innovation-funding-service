package org.innovateuk.ifs.eugrant;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.security.authentication.token.Authentication;
import org.innovateuk.ifs.commons.service.HttpHeadersUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.innovateuk.ifs.eugrant.builder.EuGrantResourceBuilder.newEuGrantResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class EuGrantRestServiceImplTest {

    @Mock
    protected RestTemplate mockRestTemplate;

    protected static String dataServicesUrl = "http://localhost/dummy";

    private static final String ANONYMOUS_AUTH_TOKEN = "ANONYMOUS_AUTH_TOKEN";

    @InjectMocks
    private EuGrantRestServiceImpl euGrantRestService;

    @Test
    public void save() {
        String baseUrl = "base";
        euGrantRestService.setServiceUrl(baseUrl);

        EuGrantResource euGrantResource = newEuGrantResource().build();
        RestResult<EuGrantResource> expected = mock(RestResult.class);

        setupPostWithRestResultAnonymousExpectations("/eu-grant", EuGrantResource.class, null, euGrantResource, OK);

        RestResult<EuGrantResource> restResult = euGrantRestService.create();
        assertTrue(restResult.isSuccess());
    }

    protected <T> ResponseEntity<T> setupPostWithRestResultAnonymousExpectations(String nonBaseUrl, Class<T> responseType, Object requestBody, T responseBody, HttpStatus responseCode) {
        ResponseEntity<T> response = new ResponseEntity<>(responseBody, responseCode);
        when(mockRestTemplate.exchange(dataServicesUrl + nonBaseUrl, POST, httpEntityForRestCallAnonymous(requestBody), responseType)).thenReturn(response);
        return response;
    }

    protected <T> HttpEntity<T> httpEntityForRestCallAnonymous(T body) {
        HttpHeaders headers = HttpHeadersUtils.getJSONHeaders();
        headers.set(Authentication.TOKEN, ANONYMOUS_AUTH_TOKEN);
        return new HttpEntity<>(body, headers);
    }

}