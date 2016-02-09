package com.worth.ifs.commons.service;

import com.worth.ifs.security.NotSecured;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

import static com.worth.ifs.commons.security.TokenAuthenticationService.AUTH_TOKEN;
import static java.util.Collections.singletonList;

/**
 * BaseRestService provides a base for all Service classes.
 */
public abstract class BaseRestService {
    private final static Log LOG = LogFactory.getLog(BaseRestService.class);

    private Supplier<RestTemplate> restTemplateSupplier = RestTemplate::new;
    private Supplier<AsyncRestTemplate> asyncRestTemplateSupplier = AsyncRestTemplate::new;

    private String dataRestServiceURL;

    protected String getDataRestServiceURL() {
        return dataRestServiceURL;
    }

    @Value("${ifs.data.service.rest.baseURL}")
    public void setDataRestServiceUrl(String dataRestServiceURL) {
        this.dataRestServiceURL = dataRestServiceURL;
    }

    public void setRestTemplateSupplier(Supplier<RestTemplate> restTemplateSupplier) {
        this.restTemplateSupplier = restTemplateSupplier;
    }

    public void setAsyncRestTemplate(Supplier<AsyncRestTemplate> asyncRestTemplateSupplier) {
        this.asyncRestTemplateSupplier = asyncRestTemplateSupplier;
    }

    public <T> ListenableFuture<ResponseEntity<T>> restGetAsync(String path, Class<T> clazz){
         return withEmptyCallback(getAsyncRestTemplate().exchange(getDataRestServiceURL() + path, HttpMethod.GET, jsonEntity(""), clazz));
    }

    private static final <T> ListenableFuture<ResponseEntity<T>> withEmptyCallback(ListenableFuture<ResponseEntity<T>> toAddTo){
        // If we do not add a callback then the behaviour of calling get on the future becomes serial instead of parallel.
        // I.e. the future will not start execution until get is called. However if an empty callback is added the future
        // will start execution immediately (or as soon as a thread is available) and we can then call get.
        toAddTo.addCallback(new ListenableFutureCallback<ResponseEntity<T>>() {
            @Override
            public void onFailure(final Throwable ex) {
                LOG.error("Failure in the empty callback: " + ex);
            }

            @Override
            public void onSuccess(final ResponseEntity<T> result) {
                // Do nothing
            }
        });
        return toAddTo;
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
        LOG.debug("restGetEntity: "+path);
        return getRestTemplate().exchange(getDataRestServiceURL() + path, HttpMethod.GET, jsonEntity(""), c);
    }

    protected  <T> ResponseEntity<T> restGetParameterizedType(String path, ParameterizedTypeReference<T> responseType){
        LOG.debug("restGetParameterizedType: "+path);
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
        LOG.debug("restPostWithEntity: "+path);
        return restPostWithEntity(path, postEntity, c).getBody();
    }

    protected void restPut(String path) {
        LOG.debug("restPutEntity: "+path);
        restPutEntity(path, Void.class);
    }

    protected <T> ResponseEntity<T> restPutEntity(String path, Class<T> c){
        return getRestTemplate().exchange(getDataRestServiceURL() + path, HttpMethod.PUT, jsonEntity(""), c);
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

    protected AsyncRestTemplate getAsyncRestTemplate() {
        return asyncRestTemplateSupplier.get();
    }
}

