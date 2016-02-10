package com.worth.ifs.commons.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.application.service.Futures;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorEnvelope;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.Errors.internalServerErrorError;
import static com.worth.ifs.commons.security.TokenAuthenticationService.AUTH_TOKEN;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

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

    protected <T> RestResult<T> getWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, GET, returnType);
    }

    protected <T> RestResult<T> getWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, GET, returnType);
    }

    protected <T> Future<RestResult<T>> getWithRestResultAsyc(String path, Class<T> returnType) {
        return exchangeWithRestResultAsync(path, GET, returnType);
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
        return getRestTemplate().exchange(getDataRestServiceURL() + path, GET, jsonEntity(null), c);
    }

    protected  <T> ResponseEntity<T> restGet(String path, ParameterizedTypeReference<T> returnType){
        LOG.debug("restGet: "+path);
        return getRestTemplate().exchange(getDataRestServiceURL() + path, GET, jsonEntity(null), returnType);
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
        return getRestTemplate().exchange(getDataRestServiceURL() + path, PUT, jsonEntity(null), c);
    }

    protected <T> RestResult<T> postWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, POST, returnType, OK, CREATED);
    }

    protected <T> RestResult<T> postWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, POST, returnType, OK, CREATED);
    }

    protected <R> RestResult<R> postWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType) {
        return exchangeObjectWithRestResult(path, POST, objectToSend, returnType, OK, CREATED);
    }

    protected <R> RestResult<R> postWithRestResult(String path, Object objectToSend, Class<R> returnType) {
        return exchangeObjectWithRestResult(path, POST, objectToSend, returnType, OK, CREATED);
    }

    protected <T> RestResult<T> putWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, PUT, returnType);
    }

    protected <T> RestResult<T> putWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, PUT, returnType);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, method, returnType, OK);
    }

    private <T> RestResult<T> exchangeObjectWithRestResult(String path, HttpMethod method, Object objectToSend, ParameterizedTypeReference<T> returnType) {
        return exchangeObjectWithRestResult(path, method, objectToSend, returnType, OK);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, Class<T> returnType) {
        return exchangeWithRestResult(path, method, returnType, OK);
    }

    private <T> Future<RestResult<T>> exchangeWithRestResultAsync(String path, HttpMethod method, Class<T> returnType) {
        return exchangeWithRestResultAsync(path, method, returnType, OK);
    }

    private <T> RestResult<T> exchangeObjectWithRestResult(String path, HttpMethod method, Object objectToSend, Class<T> returnType) {
        return exchangeObjectWithRestResult(path, method, objectToSend, returnType, OK);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, ParameterizedTypeReference<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(getDataRestServiceURL() + path, method, jsonEntity(null), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeObjectWithRestResult(String path, HttpMethod method, Object objectToSend, ParameterizedTypeReference<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(getDataRestServiceURL() + path, method, jsonEntity(objectToSend), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(getDataRestServiceURL() + path, method, jsonEntity(null), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> Future<RestResult<T>> exchangeWithRestResultAsync(String path, HttpMethod method, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResultAsync(() -> getAsyncRestTemplate().exchange(getDataRestServiceURL() + path, method, jsonEntity(null), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeObjectWithRestResult(String path, HttpMethod method, Object objectToSend, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(getDataRestServiceURL() + path, method, jsonEntity(objectToSend), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }



    private <T> RestResult<T> exchangeWithRestResult(Supplier<ResponseEntity<T>> exchangeFn, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        try {
            ResponseEntity<T> response = exchangeFn.get();
            List<HttpStatus> allExpectedSuccessStatusCodes = combineLists(asList(otherExpectedStatusCodes), expectedSuccessCode);

            if (allExpectedSuccessStatusCodes.contains(response.getStatusCode())) {
                return RestResult.<T> restSuccess(response.getBody(), response.getStatusCode());
            } else {
                return RestResult.<T> restFailure(new Error(INTERNAL_SERVER_ERROR, "Unexpected status code " + response.getStatusCode(), INTERNAL_SERVER_ERROR));
            }
        } catch (HttpStatusCodeException e) {

            return fromJson(e.getResponseBodyAsString(), RestErrorEnvelope.class).mapLeftOrRight(
                    failure -> RestResult.<T>restFailure(internalServerErrorError("Unable to process JSON response as type " + RestErrorEnvelope.class.getSimpleName())),
                    success -> RestResult.<T> restFailure(success.getErrors(), e.getStatusCode())
            );
        }
    }

    private <T> Future<RestResult<T>> exchangeWithRestResultAsync(Supplier<ListenableFuture<ResponseEntity<T>>> exchangeFn, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        Future<ResponseEntity<T>> responseEntityListenableFuture = withEmptyCallback(exchangeFn.get());
        Future<RestResult<T>> adapt = Futures.adapt(responseEntityListenableFuture, response -> {
            try {
                List<HttpStatus> allExpectedSuccessStatusCodes = combineLists(asList(otherExpectedStatusCodes), expectedSuccessCode);

                if (allExpectedSuccessStatusCodes.contains(response.getStatusCode())) {
                    return RestResult.<T>restSuccess(response.getBody(), response.getStatusCode());
                } else {
                    return RestResult.<T>restFailure(new Error(INTERNAL_SERVER_ERROR, "Unexpected status code " + response.getStatusCode(), INTERNAL_SERVER_ERROR));
                }
            } catch (HttpStatusCodeException e) {

                return fromJson(e.getResponseBodyAsString(), RestErrorEnvelope.class).mapLeftOrRight(
                        failure -> RestResult.<T>restFailure(internalServerErrorError("Unable to process JSON response as type " + RestErrorEnvelope.class.getSimpleName())),
                        success -> RestResult.<T>restFailure(success.getErrors(), e.getStatusCode())
                );
            }
        });
        return adapt;
    }

    private <T> Either<Void, T> fromJson(String json, Class<T> clazz) {

        if (Void.class.equals(clazz)) {
            return right(null);
        }

        if (String.class.equals(clazz)) {
            return Either.<Void, T> right((T) json);
        }

        try {
            return right(new ObjectMapper().readValue(json, clazz));
        } catch (IOException e) {
            return left();
        }
    }

    protected void restPut(String path, Object entity) {
        getRestTemplate().exchange(getDataRestServiceURL() + path, PUT, jsonEntity(entity), Void.class);
    }

    protected <T> ResponseEntity<T> restPut(String path, Object entity, Class<T> c) {
        return getRestTemplate().exchange(getDataRestServiceURL() + path, PUT, jsonEntity(entity), c);
    }

    protected void restDelete(String path) {
        getRestTemplate().exchange(getDataRestServiceURL() + path, DELETE, jsonEntity(null), Void.class);
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

    public static HttpHeaders getJSONHeaders() {
        //set your headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(singletonList(APPLICATION_JSON));
        return headers;
    }

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

