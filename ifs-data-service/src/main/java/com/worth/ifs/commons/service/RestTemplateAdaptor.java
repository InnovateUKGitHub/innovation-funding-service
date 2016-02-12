package com.worth.ifs.commons.service;

import com.worth.ifs.application.service.FutureAdapterWithExceptionHandling;
import com.worth.ifs.commons.rest.RestResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Future;
import java.util.function.Supplier;

import static com.worth.ifs.commons.rest.RestResult.fromException;
import static com.worth.ifs.commons.rest.RestResult.fromResponse;
import static com.worth.ifs.commons.security.TokenAuthenticationService.AUTH_TOKEN;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
public class RestTemplateAdaptor {
    private final static Log LOG = LogFactory.getLog(RestTemplateAdaptor.class);

    private Supplier<RestTemplate> restTemplateSupplier = RestTemplate::new;
    private Supplier<AsyncRestTemplate> asyncRestTemplateSupplier = AsyncRestTemplate::new;

    public void setRestTemplateSupplier(Supplier<RestTemplate> restTemplateSupplier) {
        this.restTemplateSupplier = restTemplateSupplier;
    }

    public void setAsyncRestTemplate(Supplier<AsyncRestTemplate> asyncRestTemplateSupplier) {
        this.asyncRestTemplateSupplier = asyncRestTemplateSupplier;
    }

    private RestTemplate getRestTemplate() {
        return restTemplateSupplier.get();
    }

    private AsyncRestTemplate getAsyncRestTemplate() {
        return asyncRestTemplateSupplier.get();
    }

    // Synchronous public calls
    @RestCacheResult
    public <T> RestResult<T> getWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, GET, returnType);
    }

    @RestCacheResult
    public <T> RestResult<T> getWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, GET, returnType);
    }

    @RestCacheInvalidateResult
    public <T> RestResult<T> postWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, POST, returnType, OK, CREATED);
    }

    @RestCacheInvalidateResult
    public <R> RestResult<R> postWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType) {
        return exchangeObjectWithRestResult(path, POST, objectToSend, returnType, OK, CREATED);
    }

    @RestCacheInvalidateResult
    public <T> RestResult<T> postWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, POST, returnType, OK, CREATED);
    }

    @RestCacheInvalidateResult
    public <R> RestResult<R> postWithRestResult(String path, Object objectToSend, Class<R> returnType) {
        return exchangeObjectWithRestResult(path, POST, objectToSend, returnType, OK, CREATED);
    }

    @RestCacheInvalidateResult
    public <R> RestResult<R> postWithRestResult(String path, Object objectToSend, Class<R> returnType, HttpStatus expectedStatusCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeObjectWithRestResult(path, POST, objectToSend, returnType, expectedStatusCode, otherExpectedStatusCodes);
    }

    @RestCacheInvalidateResult
    public <R> RestResult<R> postWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType, HttpStatus expectedStatusCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeObjectWithRestResult(path, POST, objectToSend, returnType, expectedStatusCode, otherExpectedStatusCodes);
    }

    @RestCacheInvalidateResult
    public <R> RestResult<R> putWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType) {
        return exchangeObjectWithRestResult(path, PUT, objectToSend, returnType, OK);
    }

    @RestCacheInvalidateResult
    public <T> RestResult<T> putWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, PUT, returnType);
    }

    @RestCacheInvalidateResult
    public <T> RestResult<T> putWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, PUT, returnType);
    }

    @RestCacheInvalidateResult
    public <R> RestResult<R> putWithRestResult(String path, Object objectToSend, Class<R> returnType) {
        return exchangeObjectWithRestResult(path, PUT, objectToSend, returnType, OK);
    }

    @RestCacheInvalidateResult
    public <T> RestResult<T> deleteWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, DELETE, returnType, OK, NO_CONTENT);
    }

    @RestCacheInvalidateResult
    public <T> RestResult<T> deleteWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, DELETE, returnType, OK, NO_CONTENT);
    }

    @RestCacheResult
    public <T> ResponseEntity<T> restGetEntity(String path, Class<T> c) {
        return getRestTemplate().exchange(path, GET, jsonEntity(null), c);
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
        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = getHeaders();

        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            headers.set(AUTH_TOKEN, SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        }

        HttpEntity<Object> entity = new HttpEntity<>(postEntity, headers);
        return restTemplate.postForEntity(path, entity, responseType);
    }

    @RestCacheInvalidateResult
    public <T> ResponseEntity<T> restPutEntity(String path, Class<T> c) {
        return getRestTemplate().exchange(path, PUT, jsonEntity(null), c);
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
    public void restDelete(String path) {
        getRestTemplate().exchange(path, DELETE, jsonEntity(null), Void.class);
    }

    // Asynchronous public calls
    @RestCacheResult
    public <T> Future<RestResult<T>> getWithRestResultAsyc(String path, Class<T> returnType) {
        return exchangeWithRestResultAsync(path, GET, returnType);
    }

    @RestCacheResult
    public <T> ListenableFuture<ResponseEntity<T>> restGetAsync(String path, Class<T> clazz) {
        return withEmptyCallback(getAsyncRestTemplate().exchange(path, HttpMethod.GET, jsonEntity(""), clazz));
    }

    // Synchronous private calls
    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, Class<T> returnType) {
        return exchangeWithRestResult(path, method, returnType, OK);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, method, returnType, OK);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, ParameterizedTypeReference<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(path, method, jsonEntity(null), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeObjectWithRestResult(String path, HttpMethod method, Object objectToSend, ParameterizedTypeReference<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(path, method, jsonEntity(objectToSend), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(path, method, jsonEntity(null), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    public <T> RestResult<T> exchangeObjectWithRestResult(String path, HttpMethod method, Object objectToSend, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(path, method, jsonEntity(objectToSend), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeWithRestResult(final Supplier<ResponseEntity<T>> exchangeFn, final HttpStatus expectedSuccessCode, final HttpStatus... otherExpectedStatusCodes) {
        try {
            return fromResponse(exchangeFn.get(), expectedSuccessCode, otherExpectedStatusCodes);
        } catch (HttpStatusCodeException e) {
            return fromException(e);
        }
    }

    // Asynchronous private calls
    private <T> Future<RestResult<T>> exchangeWithRestResultAsync(String path, HttpMethod method, Class<T> returnType) {
        return exchangeWithRestResultAsync(path, method, returnType, OK);
    }

    public <T> Future<RestResult<T>> exchangeWithRestResultAsync(String path, HttpMethod method, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResultAsync(() -> getAsyncRestTemplate().exchange(path, method, jsonEntity(null), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> Future<RestResult<T>> exchangeWithRestResultAsync(Supplier<ListenableFuture<ResponseEntity<T>>> exchangeFn, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        Future<ResponseEntity<T>> raw = withEmptyCallback(exchangeFn.get());
        return new FutureAdapterWithExceptionHandling<>(raw,
                response -> fromResponse(response, expectedSuccessCode, otherExpectedStatusCodes),
                e -> e instanceof HttpStatusCodeException ? right(fromException((HttpStatusCodeException) e)) : left());
    }

    private static final <T> ListenableFuture<ResponseEntity<T>> withEmptyCallback(ListenableFuture<ResponseEntity<T>> toAddTo) {
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

    public static HttpHeaders getJSONHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.setAccept(singletonList(APPLICATION_JSON));
        return headers;
    }

    public static HttpHeaders getHeaders() {
        return getJSONHeaders();
    }

    protected <T> HttpEntity<T> jsonEntity(T entity) {
        HttpHeaders headers = getHeaders();
        if (SecurityContextHolder.getContext() != null &&
                SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().getCredentials() != null) {
            headers.set(AUTH_TOKEN, SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
        }
        return new HttpEntity<>(entity, headers);
    }


}
