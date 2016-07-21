package com.worth.ifs.commons.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public abstract class AbstractRestTemplateAdaptor {
    private final static Log LOG = LogFactory.getLog(AbstractRestTemplateAdaptor.class);

    private final static ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    protected AsyncRestTemplate getAsyncRestTemplate() {
        return asyncRestTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setAsyncRestTemplate(AsyncRestTemplate asyncRestTemplate) {
        this.asyncRestTemplate = asyncRestTemplate;
    }

    // Synchronous public calls
    @RestCacheResult
    public <T> ResponseEntity<T> restGetEntity(String path, Class<T> c) {
        return getRestTemplate().exchange(path, GET, jsonEntity(null), c);
    }

    @RestCacheResult
    public <T> ResponseEntity<T> restGetEntity(String path, Class<T> c, Map<String, ?> uriVariables) {
        return getRestTemplate().exchange(path, GET, jsonEntity(null), c, uriVariables);
    }
    @RestCacheResult
    public <T> ResponseEntity<T> restGetEntity(String path, Class<T> c, HttpHeaders headers) {
        return getRestTemplate().exchange(path, GET, new HttpEntity<Object>(null, headers), c);
    }

    @RestCacheResult
    public <T> ResponseEntity<T> restGet(String path, ParameterizedTypeReference<T> returnType) {
        return getRestTemplate().exchange(path, GET, jsonEntity(null), returnType);
    }

    @RestCacheInvalidateResult
    public <T> ResponseEntity<T> restPostWithEntity(String path, Object postEntity, Class<T> responseType) {
        return getRestTemplate().postForEntity(path, jsonEntity(postEntity), responseType);
    }

    @RestCacheInvalidateResult
    public <T, R> Either<ResponseEntity<R>, ResponseEntity<T>> restPostWithEntity(String path, Object postEntity, Class<T> responseType, Class<R> failureType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        try {
            ResponseEntity<String> asString = restPostWithEntity(path, postEntity, String.class);
            return handleSuccessOrFailureJsonResponse(asString, responseType, failureType, expectedSuccessCode, otherExpectedStatusCodes);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
        	LOG.error(e);
            return handleSuccessOrFailureJsonResponse(new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode()), responseType, failureType, expectedSuccessCode, otherExpectedStatusCodes);
        }
    }

    @RestCacheInvalidateResult
    public <T> ResponseEntity<T> restPutWithEntity(String path, Class<T> c) {
        return getRestTemplate().exchange(path, PUT, jsonEntity(null), c);
    }

    @RestCacheInvalidateResult
    public <T> ResponseEntity<T> restPutWithEntity(String path, Object putEntity, Class<T> c) {
        return getRestTemplate().exchange(path, PUT, jsonEntity(putEntity), c);
    }

    @RestCacheInvalidateResult
    public void restPut(String path, Object entity) {
        getRestTemplate().exchange(path, PUT, jsonEntity(entity), Void.class);
    }

    @RestCacheInvalidateResult
    public <T> ResponseEntity<T> restPut(String path, Object entity, Class<T> c) {
        return getRestTemplate().exchange(path, PUT, jsonEntity(entity), c);
    }

    @RestCacheInvalidateResult
    public <T, R> Either<ResponseEntity<R>, ResponseEntity<T>> restPutWithEntity(String path, Object putEntity, Class<T> responseType, Class<R> failureType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        try {
            ResponseEntity<String> asString = restPutWithEntity(path, putEntity, String.class);
            return handleSuccessOrFailureJsonResponse(asString, responseType, failureType, expectedSuccessCode, otherExpectedStatusCodes);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
        	LOG.error(e);
            return handleSuccessOrFailureJsonResponse(new ResponseEntity<>(e.getResponseBodyAsString(), e.getStatusCode()), responseType, failureType, expectedSuccessCode, otherExpectedStatusCodes);
        }
    }

    @RestCacheInvalidateResult
    public void restDelete(String path) {
        getRestTemplate().exchange(path, DELETE, jsonEntity(null), Void.class);
    }

    // Asynchronous public calls
    @RestCacheResult
    public <T> ListenableFuture<ResponseEntity<T>> restGetAsync(String path, Class<T> clazz) {
        return withEmptyCallback(getAsyncRestTemplate().exchange(path, HttpMethod.GET, jsonEntity(""), clazz));
    }

    protected final <T, R> Either<ResponseEntity<R>, ResponseEntity<T>> handleSuccessOrFailureJsonResponse(ResponseEntity<String> asString, Class<T> responseType, Class<R> failureType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        List<HttpStatus> allExpectedSuccessStatusCodes = combineLists(asList(otherExpectedStatusCodes), expectedSuccessCode);

        if (allExpectedSuccessStatusCodes.contains(asString.getStatusCode())) {
            return fromJson(asString.getBody(), responseType).mapLeftOrRight(
                    failure -> left(new ResponseEntity<>((R) null, INTERNAL_SERVER_ERROR)),
                    success -> right(new ResponseEntity<>(success, asString.getStatusCode()))
            );
        } else {
            return fromJson(asString.getBody(), failureType).mapLeftOrRight(
                    failure -> left(new ResponseEntity<>((R) null, INTERNAL_SERVER_ERROR)),
                    success -> left(new ResponseEntity<>(success, asString.getStatusCode()))
            );
        }
    }

    protected final <T> ListenableFuture<ResponseEntity<T>> withEmptyCallback(ListenableFuture<ResponseEntity<T>> toAddTo) {
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

    private <T> Either<Void, T> fromJson(String json, Class<T> responseType) {
        if (Void.class.isAssignableFrom(responseType) && isBlank(json)) {
            return right(null);
        }
        try {
            return right(objectMapper.readValue(json, responseType));
        } catch (IOException e) {
        	LOG.error(e);
            return left();
        }
    }

    public abstract HttpHeaders getHeaders();


    public <T> HttpEntity<T> jsonEntity(T entity){
        return new HttpEntity<>(entity, getHeaders());
    }

    public <T> HttpEntity<T> jsonEntity(T entity, HttpHeaders additionalHeaders){
        HttpHeaders standardHeaders = getHeaders();
        standardHeaders.putAll(additionalHeaders);
        return new HttpEntity<>(entity, standardHeaders);
    }

}
