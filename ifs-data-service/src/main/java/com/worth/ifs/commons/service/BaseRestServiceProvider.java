package com.worth.ifs.commons.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.worth.ifs.commons.security.TokenAuthenticationService.AUTH_TOKEN;

/**
 * BaseRestServiceProvider provides a base for all Service classes.
 */
public abstract class BaseRestServiceProvider {

    protected String dataRestServiceURL;

    private Supplier<RestTemplate> restTemplateSupplier = () -> new RestTemplate();

    @Value("${ifs.data.service.rest.baseURL}")
    public void setDataRestServiceUrl(String dataRestServiceURL) {
        this.dataRestServiceURL = dataRestServiceURL;
    }

    public void setRestTemplateSupplier(Supplier<RestTemplate> restTemplateSupplier) {
        this.restTemplateSupplier = restTemplateSupplier;
    }

    /**
     * restGet is a generic method that performs a RESTful GET request.
     *
     * @param path - the unified name resource of the request to be made
     * @param c - the class type of that the requestor wants to get from the request response.
     * @param <T>
     * @return
     */
    protected <T> T restGet(String path, Class<T> c) {
        ResponseEntity<T> responseEntity = restGetEntity(path, c);
        return responseEntity.getBody();
    }

    /**
     * restGet is a generic method that performs a RESTful GET request.
     *
     * @param path - the unified name resource of the request to be made
     * @param c - the class type of that the requestor wants to get from the request response.
     * @param <T>
     * @return
     */
    protected <T> ResponseEntity<T> restGetEntity(String path, Class<T> c) {

        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = getJSONHeaders();

        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            headers.set(AUTH_TOKEN, SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        }

        HttpEntity<String> entity = new HttpEntity<>("", headers);
        return restTemplate.exchange(dataRestServiceURL + path , HttpMethod.GET, entity, c);
    }

    /**
     * restPost is a generic method that performs a RESTful POST request.
     *
     * @param path - the unified name resource of the request to be made
     * @param c - the class type of that the requestor wants to get from the request response.
     * @param <T>
     * @return
     */
    protected <T> T restPost(String path, Object postEntity, Class<T> c) {
        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = getJSONHeaders();

        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            headers.set(AUTH_TOKEN, SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        }

        HttpEntity<Object> entity = new HttpEntity<>(postEntity, getJSONHeaders());
        ResponseEntity<T> response = restTemplate.postForEntity(dataRestServiceURL + path, entity, c);
        return response.getBody();
    }

    public static HttpHeaders getJSONHeaders() {
        //set your headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    protected RestTemplate getRestTemplate() {
        return restTemplateSupplier.get();
    }
}

