package org.innovateuk.ifs.commons.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.innovateuk.ifs.commons.rest.RestResult.fromException;
import static org.innovateuk.ifs.commons.rest.RestResult.fromResponse;
import static org.innovateuk.ifs.util.Either.left;
import static org.innovateuk.ifs.util.Either.right;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

@Component
public abstract class AbstractInternalRestTemplateAdaptor extends AbstractRestTemplateAdaptor {

    public AbstractInternalRestTemplateAdaptor() {
    }

    public AbstractInternalRestTemplateAdaptor(String baseUrl) {
        super(baseUrl);
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
    public <R> RestResult<R> postWithRestResult(String path, Object objectToSend, HttpHeaders additionalHeaders, Class<R> returnType) {
        return exchangeObjectWithRestResult(path, POST, objectToSend, additionalHeaders, returnType, OK, CREATED);
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

    // Asynchronous public calls
    @RestCacheResult
    public <T> CompletableFuture<RestResult<T>> getWithRestResultAsyc(String path, Class<T> returnType) {
        return exchangeWithRestResultAsync(path, GET, returnType);
    }

    @RestCacheResult
    public <T> CompletableFuture<RestResult<T>> getWithRestResultAsyc(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResultAsync(path, GET, returnType);
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

    private <T> RestResult<T> exchangeObjectWithRestResult(String path, HttpMethod method, Object objectToSend, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(path, method, jsonEntity(objectToSend), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeObjectWithRestResult(String path, HttpMethod method, Object objectToSend, HttpHeaders additionalHeaders, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResult(() -> getRestTemplate().exchange(path, method, jsonEntity(objectToSend, additionalHeaders), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeWithRestResult(final Supplier<ResponseEntity<T>> exchangeFn, final HttpStatus expectedSuccessCode, final HttpStatus... otherExpectedStatusCodes) {
        try {
            return fromResponse(exchangeFn.get(), expectedSuccessCode, otherExpectedStatusCodes);
        } catch (HttpStatusCodeException e) {
            return fromException(e);
        }
    }

    // Asynchronous private calls
    private <T> CompletableFuture<RestResult<T>> exchangeWithRestResultAsync(String path, HttpMethod method, Class<T> returnType) {
        return exchangeWithRestResultAsync(path, method, returnType, OK);
    }

    private <T> CompletableFuture<RestResult<T>> exchangeWithRestResultAsync(String path, HttpMethod method, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResultAsync(path, method, returnType, OK);
    }

    private <T> CompletableFuture<RestResult<T>> exchangeWithRestResultAsync(String path, HttpMethod method, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResultAsync(() -> getAsyncRestTemplate().exchange(path, method, jsonEntity(null), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> CompletableFuture<RestResult<T>> exchangeWithRestResultAsync(String path, HttpMethod method, ParameterizedTypeReference<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return exchangeWithRestResultAsync(() -> getAsyncRestTemplate().exchange(path, method, jsonEntity(null), returnType), expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> CompletableFuture<RestResult<T>> exchangeWithRestResultAsync(Supplier<ListenableFuture<ResponseEntity<T>>> exchangeFn, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {

        ListenableFuture<ResponseEntity<T>> raw = withEmptyCallback(exchangeFn.get());

        ListenableFuture<RestResult<T>> listenableFuture = new FutureAdapterWithExceptionHandling<>(raw,
                response -> fromResponse(response, expectedSuccessCode, otherExpectedStatusCodes),
                e -> e instanceof HttpStatusCodeException ? right(fromException((HttpStatusCodeException) e)) : left());

        return buildCompletableFutureFromListenableFuture(listenableFuture);
    }

    @Override
    public final HttpHeaders getHeaders() {
        HttpHeaders headers = HttpHeadersUtils.getJSONHeaders();
        setAuthenticationToken(headers);
        return headers;
    }

    protected abstract void setAuthenticationToken(HttpHeaders headers);
}
