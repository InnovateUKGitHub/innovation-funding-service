package com.worth.ifs.commons.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.function.Supplier;

import static com.worth.ifs.commons.security.UidAuthenticationService.AUTH_TOKEN;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * BaseRestService provides a base for all Service classes.
 */
public abstract class BaseRestService {
    private final Log log = LogFactory.getLog(getClass());

    private Supplier<RestTemplate> restTemplateSupplier = RestTemplate::new;

    private String dataRestServiceURL;

    protected String getDataRestServiceURL() {
        return dataRestServiceURL;
    }

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
        log.debug("restGetEntity: "+path);
        return getRestTemplate().exchange(getDataRestServiceURL() + path, HttpMethod.GET, jsonEntity(""), c);
    }

    protected  <T> ResponseEntity<T> restGetParameterizedType(String path, ParameterizedTypeReference<T> responseType){
        log.debug("restGetParameterizedType: "+path);
        return getRestTemplate().exchange(getDataRestServiceURL() + path, HttpMethod.GET, jsonEntity(""), responseType);
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
        log.debug("restPostWithEntity: "+path);
        return restPostWithEntity(path, postEntity, c).getBody();
    }

    /**
     * restPost is a generic method that performs a RESTful POST request.
     *
     * @param path - the unified name resource of the request to be made
     * @param successClass - the class type of that the requestor wants to get from the request response, if a success response is encountered
     * @param failureClass - the class type of that the requestor wants to get from the request response, if a failure response is encountered
     * @param successCodes - a set of codes that represent a success case
     * @param <T>
     * @return
     */
    protected <R, T> Either<R, T> restPost(String path, Object postEntity, Class<T> successClass, Class<R> failureClass, HttpStatus... successCodes) {
        log.debug("restPostWithEntity: "+path);
        ResponseEntity<String> response = restPostWithEntity(path, postEntity, String.class);

        if (asList(successCodes).contains(response.getStatusCode())) {
            return right(fromJson(response.getBody(), successClass));
        }

        return left(fromJson(response.getBody(), failureClass));
    }

    protected void restPut(String path) {
        log.debug("restPutEntity: "+path);
        restPutEntity(path, Void.class);
    }

    protected <T> ResponseEntity<T> restPutEntity(String path, Class<T> c){
        return getRestTemplate().exchange(getDataRestServiceURL() + path, HttpMethod.PUT, jsonEntity(""), c);
    }

    protected <R, T> Either<R, T> restPut(String path, Object putEntity, Class<T> successClass, Class<R> failureClass, HttpStatus successCodes){

        ResponseEntity<String> response = getRestTemplate().exchange(getDataRestServiceURL() + path, HttpMethod.PUT, jsonEntity(putEntity), String.class);

        if (asList(successCodes).contains(response.getStatusCode())) {
            return right(fromJson(response.getBody(), successClass));
        }

        return left(fromJson(response.getBody(), failureClass));
    }

    protected void restPut(String path, Object entity) {
        getRestTemplate().exchange(getDataRestServiceURL() + path, HttpMethod.PUT, jsonEntity(entity), Void.class);
    }

    protected <T> ResponseEntity<T> restPut(String path, Object entity, Class<T> c) {
        return getRestTemplate().exchange(getDataRestServiceURL() + path, HttpMethod.PUT, jsonEntity(entity), c);
    }

    protected void restDelete(String path) {
        getRestTemplate().exchange(getDataRestServiceURL() + path, HttpMethod.DELETE, jsonEntity(""), Void.class);
    }


    /**
     * restPost is a generic method that performs a RESTful POST request.
     *
     * @param path - the unified name resource of the request to be made
     * @param responseType - the class type of that the requestor wants to get from the request response.
     * @param <T>
     * @return
     */
    protected <T> ResponseEntity<T> restPostWithEntity(String path, Object postEntity, Class<T> responseType) {
        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = getHeaders();

        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            headers.set(AUTH_TOKEN, SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        }

        HttpEntity<Object> entity = new HttpEntity<>(postEntity, headers);
        return restTemplate.postForEntity(getDataRestServiceURL() + path, entity, responseType);
    }

    protected <T> T fromJson(String json, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(json, clazz);
        } catch (IOException e) {
            log.error("Unable to resolve class " + clazz + " from JSON " + json, e);
            return null;
        }
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

