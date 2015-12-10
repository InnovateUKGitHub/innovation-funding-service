package com.worth.ifs.commons.service;

import com.worth.ifs.security.NotSecured;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.function.Supplier;

import static com.worth.ifs.commons.security.TokenAuthenticationService.AUTH_TOKEN;
import static java.util.Collections.singletonList;

/**
 * BaseRestService provides a base for all Service classes.
 */
public abstract class BaseRestService {
    private final Log log = LogFactory.getLog(getClass());

    private Supplier<RestTemplate> restTemplateSupplier = RestTemplate::new;

    protected String dataRestServiceURL;

    @NotSecured("")
    @Value("${ifs.data.service.rest.baseURL}")
    public void setDataRestServiceUrl(String dataRestServiceURL) {
        this.dataRestServiceURL = dataRestServiceURL;
    }

    @NotSecured("")
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
        return getRestTemplate().exchange(dataRestServiceURL + path, HttpMethod.GET, jsonEntity(""), c);
    }

    protected  <T> ResponseEntity<T> restGetParameterizedType(String path, ParameterizedTypeReference<T> responseType){
        return getRestTemplate().exchange(URI.create(path), HttpMethod.GET, jsonEntity(""), responseType);
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
        return restPostWithEntity(path, postEntity, c).getBody();
    }

    protected void restPut(String path) {
        restPutEntity(path, Void.class);
    }

    protected <T> ResponseEntity<T> restPutEntity(String path, Class<T> c){
        return getRestTemplate().exchange(dataRestServiceURL + path, HttpMethod.PUT, jsonEntity(""), c);
    }

    protected void restPut(String path, Object entity) {
        getRestTemplate().exchange(dataRestServiceURL + path, HttpMethod.PUT, jsonEntity(entity), Void.class);
    }

    protected <T> ResponseEntity<T> restPut(String path, Object entity, Class<T> c) {
        return getRestTemplate().exchange(dataRestServiceURL + path, HttpMethod.PUT, jsonEntity(entity), c);
    }

    protected void restDelete(String path) {
        getRestTemplate().exchange(dataRestServiceURL + path, HttpMethod.DELETE, jsonEntity(""), Void.class);
    }


    /**
     * restPost is a generic method that performs a RESTful POST request.
     *
     * @param path - the unified name resource of the request to be made
     * @param c - the class type of that the requestor wants to get from the request response.
     * @param <T>
     * @return
     */
    protected <T> ResponseEntity<T> restPostWithEntity(String path, Object postEntity, Class<T> c) {
        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = getHeaders();

        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            headers.set(AUTH_TOKEN, SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        }

        HttpEntity<Object> entity = new HttpEntity<>(postEntity, headers);
        return restTemplate.postForEntity(dataRestServiceURL + path, entity, c);
    }

    @NotSecured("")
    public static HttpHeaders getJSONHeaders() {
        //set your headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    @NotSecured("")
    public HttpHeaders getHeaders(){
        return getJSONHeaders();
    }


    protected <T> HttpEntity<T> jsonEntity(T entity){
        HttpHeaders headers = getHeaders();
        if (SecurityContextHolder.getContext() != null &&
            SecurityContextHolder.getContext().getAuthentication() != null &&
            SecurityContextHolder.getContext().getAuthentication().getCredentials() != null) {
            headers.set(AUTH_TOKEN, SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        }
        return new HttpEntity<>(entity, headers);
    }

    protected RestTemplate getRestTemplate() {
        return restTemplateSupplier.get();
    }
}

