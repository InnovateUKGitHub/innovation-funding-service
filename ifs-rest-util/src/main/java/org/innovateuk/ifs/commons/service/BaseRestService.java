package org.innovateuk.ifs.commons.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.CompletableFuture;

import static java.util.Collections.singletonList;

/**
 * BaseRestService provides a base for all Service classes.
 */
public abstract class BaseRestService {

    @Autowired
    private RootDefaultRestTemplateAdaptor adaptor;

    @Autowired
    private RootAnonymousUserRestTemplateAdaptor anonymousRestTemplateAdaptor;

    protected String serviceUrl;

    private String getServiceUrl() {
        return serviceUrl;
    }

    @Value("${ifs.data.service.rest.baseURL}")
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setRestTemplateAdaptor(RootDefaultRestTemplateAdaptor adaptor) {
        this.adaptor = adaptor;
    }

    public void setAnonymousRestTemplateAdaptor(RootAnonymousUserRestTemplateAdaptor anonymousRestTemplateAdaptor) {
        this.anonymousRestTemplateAdaptor = anonymousRestTemplateAdaptor;
    }

    // Synchronous calls
    protected <T> RestResult<T> getWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.getWithRestResult(getServiceUrl() + path, returnType);
    }

    protected <T> RestResult<T> getWithRestResultAnonymous(String path, ParameterizedTypeReference<T> returnType) {
        return anonymousRestTemplateAdaptor.getWithRestResult(getServiceUrl() + path, returnType);
    }

    protected <T> RestResult<T> getWithRestResult(String path, Class<T> returnType) {
        return adaptor.getWithRestResult(getServiceUrl() + path, returnType);
    }

    protected <T> RestResult<T> getWithRestResultAnonymous(String path, Class<T> returnType) {
        return anonymousRestTemplateAdaptor.getWithRestResult(getServiceUrl() + path, returnType);
    }

    protected <T> RestResult<T> putWithRestResultAnonymous(String path, Class<T> returnType) {
        return anonymousRestTemplateAdaptor.putWithRestResult(getServiceUrl() + path, returnType);
    }

    protected <T> RestResult<T> postWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.postWithRestResult(getServiceUrl() + path, returnType);
    }

    protected RestResult<Void> postWithRestResult(String path) {
        return postWithRestResult(path, Void.class);
    }

    protected <T> RestResult<T> postWithRestResult(String path, Class<T> returnType) {
        return adaptor.postWithRestResult(getServiceUrl() + path, returnType);
    }

    protected <R> RestResult<R> postWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType) {
        return adaptor.postWithRestResult(getServiceUrl() + path, objectToSend, returnType);
    }

    protected <R> RestResult<R> postWithRestResult(String path, Object objectToSend, Class<R> returnType) {
        return adaptor.postWithRestResult(getServiceUrl() + path, objectToSend, returnType);
    }

    protected <R> RestResult<R> postWithRestResultAnonymous(String path, Object objectToSend, Class<R> returnType) {
        return anonymousRestTemplateAdaptor.postWithRestResult(getServiceUrl() + path, objectToSend, returnType);
    }

    protected <R> RestResult<R> postWithRestResultAnonymous(String path, Class<R> returnType) {
        return anonymousRestTemplateAdaptor.postWithRestResult(getServiceUrl() + path, returnType);
    }

    protected <R> RestResult<R> postWithRestResult(String path, Object objectToSend, HttpHeaders additionalHeaders, Class<R> returnType) {
        return adaptor.postWithRestResult(getServiceUrl() + path, objectToSend, additionalHeaders, returnType);
    }

    protected <R> RestResult<R> postWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType, HttpStatus expectedStatusCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.postWithRestResult(getServiceUrl() + path, objectToSend, returnType, expectedStatusCode, otherExpectedStatusCodes);
    }

    protected <R> RestResult<R> postWithRestResult(String path, Object objectToSend, Class<R> returnType, HttpStatus expectedStatusCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.postWithRestResult(getServiceUrl() + path, objectToSend, returnType, expectedStatusCode, otherExpectedStatusCodes);
    }

    protected <T> RestResult<T> putWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.putWithRestResult(getServiceUrl() + path, returnType);
    }

    protected <T> RestResult<T> putWithRestResult(String path, Class<T> returnType) {
        return adaptor.putWithRestResult(getServiceUrl() + path, returnType);
    }

    protected <R> RestResult<R> putWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType) {
        return adaptor.putWithRestResult(getServiceUrl() + path, objectToSend, returnType);
    }

    protected <R> RestResult<R> putWithRestResult(String path, Object objectToSend, Class<R> returnType) {
        return adaptor.putWithRestResult(getServiceUrl() + path, objectToSend, returnType);
    }

    protected <R> RestResult<R> putWithRestResultAnonymous(String path, Object objectToSend, Class<R> returnType) {
        return anonymousRestTemplateAdaptor.putWithRestResult(getServiceUrl() + path, objectToSend, returnType);
    }

    protected <T> RestResult<T> deleteWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.deleteWithRestResult(getServiceUrl() + path, returnType);
    }

    protected <T> RestResult<T> deleteWithRestResult(String path, Class<T> returnType) {
        return adaptor.deleteWithRestResult(getServiceUrl() + path, returnType);
    }

    protected RestResult<Void> deleteWithRestResult(String path) {
        return adaptor.deleteWithRestResult(getServiceUrl() + path, Void.class);
    }

    protected <T> T restGet(String path, Class<T> c) {
        return restGetEntity(path, c).getBody();
    }

    protected <T> T restGetAnonymous(String path, Class<T> c) {
        return restGetEntityAnonymous(path, c).getBody();
    }

    protected <T> ResponseEntity<T> restGetEntity(String path, Class<T> c) {
        return adaptor.restGetEntity(getServiceUrl() + path, c);
    }

    protected <T> ResponseEntity<T> restGetEntityAnonymous(String path, Class<T> c) {
        return anonymousRestTemplateAdaptor.restGetEntity(getServiceUrl() + path, c);
    }

    protected <T> ResponseEntity<T> restGetEntity(String path, Class<T> c, HttpHeaders headers) {
        return adaptor.restGetEntity(getServiceUrl() + path, c, headers);
    }

    protected <T> ResponseEntity<T> restGet(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.restGet(getServiceUrl() + path, returnType);
    }

    protected <T> T restPost(String path, Object postEntity, Class<T> c) {
        return restPostWithEntity(path, postEntity, c).getBody();
    }

    protected <T, R> Either<ResponseEntity<R>, ResponseEntity<T>> restPostWithEntity(String path, Object postEntity, Class<T> responseType, Class<R> failureType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.restPostWithEntity(getServiceUrl() + path, postEntity, responseType, failureType, expectedSuccessCode, otherExpectedStatusCodes);
    }

    protected <T> ResponseEntity<T> restPostWithEntity(String path, Object postEntity, Class<T> responseType) {
        return adaptor.restPostWithEntity(getServiceUrl() + path, postEntity, responseType);
    }

    protected void restPut(String path) {
        adaptor.restPutWithEntity(getServiceUrl() + path, Void.class);
    }

    protected <T> ResponseEntity<T> restPutEntity(String path, Class<T> c) {
        return adaptor.restPutWithEntity(getServiceUrl() + path, c);
    }

    protected void restPut(String path, Object entity) {
        adaptor.restPut(getServiceUrl() + path, entity);
    }

    protected <T> ResponseEntity<T> restPut(String path, Object entity, Class<T> c) {
        return adaptor.restPut(getServiceUrl() + path, entity, c);
    }

    protected <T, R> Either<ResponseEntity<R>, ResponseEntity<T>> restPutWithEntity(String path, Object postEntity, Class<T> responseType, Class<R> failureType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.restPutWithEntity(getServiceUrl() + path, postEntity, responseType, failureType, expectedSuccessCode, otherExpectedStatusCodes);
    }

    protected void restDelete(String path) {
        adaptor.restDelete(getServiceUrl() + path);
    }

    // Asynchronous public calls
    protected <T> CompletableFuture<RestResult<T>> getWithRestResultAsync(String path, Class<T> returnType) {
        return adaptor.getWithRestResultAsyc(getServiceUrl() + path, returnType);
    }

    protected <T> CompletableFuture<RestResult<T>> getWithRestResultAsync(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.getWithRestResultAsyc(getServiceUrl() + path, returnType);
    }

    public <T> CompletableFuture<ResponseEntity<T>> restGetAsync(String path, Class<T> clazz) {
        return adaptor.restGetAsync(getServiceUrl() + path, clazz);
    }

    protected <T> CompletableFuture<RestResult<T>> getWithRestResultAsyncAnonymous(String path, Class<T> returnType) {
        return anonymousRestTemplateAdaptor.getWithRestResultAsyc(getServiceUrl() + path, returnType);
    }

    protected HttpHeaders createFileUploadHeader(String contentType, long contentLength){
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }

    public static String buildPaginationUri(String url, Integer pageNumber, Integer pageSize, String sort, MultiValueMap<String, String> params, Object... uriParameters ) {
        if (pageNumber != null) {
            params.put("page", singletonList(pageNumber.toString()));
        }
        if (pageSize != null) {
            params.put("size", singletonList(pageSize.toString()));
        }
        if (sort != null) {
            params.put("sort", singletonList(sort));
        }
        return UriComponentsBuilder.fromPath(url).queryParams(params).buildAndExpand(uriParameters).encode().toUriString();
    }

    public static String buildPaginationUri(String url, Integer pageNumber, Integer pageSize, Object... uriParameters ) {
        return buildPaginationUri(url, pageNumber, pageSize, null, new LinkedMultiValueMap<>(), uriParameters);
    }
}

