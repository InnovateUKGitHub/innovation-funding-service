package com.worth.ifs;

import com.worth.ifs.commons.service.BaseRestService;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.client.RestTemplate;

import static com.worth.ifs.commons.security.TokenAuthenticationService.AUTH_TOKEN;
import static com.worth.ifs.commons.service.BaseRestService.getJSONHeaders;

/**
 * This is the base class for testing REST services with mock components.  In addition to the standard mocks provided,
 * this base class also provides a dummy dataServiceUrl and a mock restTemplate for testing and stubbing the routes
 * that the REST services use to exchange data with the "data" layer.
 *
 * Created by dwatson on 02/10/15.
 */
public abstract class BaseRestServiceMocksTest<ServiceType extends BaseRestService> extends BaseUnitTestMocksTest {

    @Mock
    protected RestTemplate mockRestTemplate;

    protected ServiceType service;

    protected abstract ServiceType registerRestServiceUnderTest();

    protected static String dataServicesUrl;

    private static final String VALID_AUTH_TOKEN = "VALID_AUTH_TOKEN";

    @Value("${ifs.data.service.rest.baseURL}")
    public void setDataRestServiceUrl(String dataRestServiceURL) {
        this.dataServicesUrl = dataRestServiceURL;
    }

    @Override
    public void setUp() {

        super.setUp();

        service = registerRestServiceUnderTest();
        service.setDataRestServiceUrl(dataServicesUrl);
        service.setRestTemplateSupplier(() -> mockRestTemplate);

        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(new TestingAuthenticationToken("A_PRINCIPAL", VALID_AUTH_TOKEN));
        SecurityContextHolder.setContext(securityContext);
    }

    protected <T> HttpEntity<T> httpEntityForRestCall(T body) {
        HttpHeaders headers = getJSONHeaders();
        headers.set(AUTH_TOKEN, VALID_AUTH_TOKEN);
        return new HttpEntity<>(body, headers);
    }

    protected <T> HttpEntity<T> httpEntityForRestCallWithoutAuthToken(T body) {
        HttpHeaders headers = getJSONHeaders();
        return new HttpEntity<>(body, headers);
    }

    protected HttpEntity<String> httpEntityForRestCall() {
        return httpEntityForRestCall("");
    }

    protected HttpEntity<String> httpEntityForRestGetWithoutAuthToken() {
        return httpEntityForRestCallWithoutAuthToken("");
    }
}