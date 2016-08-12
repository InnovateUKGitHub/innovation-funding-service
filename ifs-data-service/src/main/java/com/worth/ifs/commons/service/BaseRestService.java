package com.worth.ifs.commons.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.util.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Future;

import static java.util.Collections.singletonList;

/**
 * BaseRestService provides a base for all Service classes.
 */
public abstract class BaseRestService {

    @Autowired
    private RestTemplateAdaptor adaptor;

    @Autowired
    private AnonymousUserRestTemplateAdaptor anonymousRestTemplateAdaptor;

    private String dataRestServiceURL;

    protected String getDataRestServiceURL() {
        return dataRestServiceURL;
    }

    @Value("${ifs.data.service.rest.baseURL}")
    public void setDataRestServiceUrl(String dataRestServiceURL) {
        this.dataRestServiceURL = dataRestServiceURL;
    }

    public void setRestTemplateAdaptor(RestTemplateAdaptor adaptor) {
        this.adaptor = adaptor;
    }

    public void setAnonymousRestTemplateAdaptor(AnonymousUserRestTemplateAdaptor anonymousRestTemplateAdaptor) {
        this.anonymousRestTemplateAdaptor = anonymousRestTemplateAdaptor;
    }

    // Synchronous calls
    protected <T> RestResult<T> getWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.getWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <T> RestResult<T> getWithRestResultAnonymous(String path, ParameterizedTypeReference<T> returnType) {
        return anonymousRestTemplateAdaptor.getWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <T> RestResult<T> getWithRestResult(String path, Class<T> returnType) {
        return adaptor.getWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <T> RestResult<T> getWithRestResultAnonymous(String path, Class<T> returnType) {
        return anonymousRestTemplateAdaptor.getWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <T> RestResult<T> putWithRestResultAnonymous(String path, Class<T> returnType) {
        return anonymousRestTemplateAdaptor.putWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <T> RestResult<T> postWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.postWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <T> RestResult<T> postWithRestResult(String path, Class<T> returnType) {
        return adaptor.postWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <R> RestResult<R> postWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType) {
        return adaptor.postWithRestResult(getDataRestServiceURL() + path, objectToSend, returnType);
    }

    protected <R> RestResult<R> postWithRestResult(String path, Object objectToSend, Class<R> returnType) {
        return adaptor.postWithRestResult(getDataRestServiceURL() + path, objectToSend, returnType);
    }

    protected <R> RestResult<R> postWithRestResultAnonymous(String path, Object objectToSend, Class<R> returnType) {
        return anonymousRestTemplateAdaptor.postWithRestResult(getDataRestServiceURL() + path, objectToSend, returnType);
    }

    protected <R> RestResult<R> postWithRestResultAnonymous(String path, Class<R> returnType) {
        return anonymousRestTemplateAdaptor.postWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <R> RestResult<R> postWithRestResult(String path, Object objectToSend, HttpHeaders additionalHeaders, Class<R> returnType) {
        return adaptor.postWithRestResult(getDataRestServiceURL() + path, objectToSend, additionalHeaders, returnType);
    }

    protected <R> RestResult<R> postWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType, HttpStatus expectedStatusCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.postWithRestResult(getDataRestServiceURL() + path, objectToSend, returnType, expectedStatusCode, otherExpectedStatusCodes);
    }

    protected <R> RestResult<R> postWithRestResult(String path, Object objectToSend, Class<R> returnType, HttpStatus expectedStatusCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.postWithRestResult(getDataRestServiceURL() + path, objectToSend, returnType, expectedStatusCode, otherExpectedStatusCodes);
    }

    protected <T> RestResult<T> putWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.putWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <T> RestResult<T> putWithRestResult(String path, Class<T> returnType) {
        return adaptor.putWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <R> RestResult<R> putWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType) {
        return adaptor.putWithRestResult(getDataRestServiceURL() + path, objectToSend, returnType);
    }

    protected <R> RestResult<R> putWithRestResult(String path, Object objectToSend, Class<R> returnType) {
        return adaptor.putWithRestResult(getDataRestServiceURL() + path, objectToSend, returnType);
    }

    protected <R> RestResult<R> putWithRestResultAnonymous(String path, Object objectToSend, Class<R> returnType) {
        return anonymousRestTemplateAdaptor.putWithRestResult(getDataRestServiceURL() + path, objectToSend, returnType);
    }

    protected <T> RestResult<T> deleteWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.deleteWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <T> RestResult<T> deleteWithRestResult(String path, Class<T> returnType) {
        return adaptor.deleteWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected RestResult<Void> deleteWithRestResult(String path) {
        return adaptor.deleteWithRestResult(getDataRestServiceURL() + path, Void.class);
    }

    protected <T> T restGet(String path, Class<T> c) {
        return restGetEntity(path, c).getBody();
    }

    protected <T> T restGetAnonymous(String path, Class<T> c) {
        return restGetEntityAnonymous(path, c).getBody();
    }

    protected <T> ResponseEntity<T> restGetEntity(String path, Class<T> c) {
        return adaptor.restGetEntity(getDataRestServiceURL() + path, c);
    }

    protected <T> ResponseEntity<T> restGetEntityAnonymous(String path, Class<T> c) {
        return anonymousRestTemplateAdaptor.restGetEntity(getDataRestServiceURL() + path, c);
    }

    protected <T> ResponseEntity<T> restGetEntity(String path, Class<T> c, HttpHeaders headers) {
        return adaptor.restGetEntity(getDataRestServiceURL() + path, c, headers);
    }

    protected <T> ResponseEntity<T> restGet(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.restGet(getDataRestServiceURL() + path, returnType);
    }

    protected <T> T restPost(String path, Object postEntity, Class<T> c) {
        return restPostWithEntity(path, postEntity, c).getBody();
    }

    protected <T, R> Either<ResponseEntity<R>, ResponseEntity<T>> restPostWithEntity(String path, Object postEntity, Class<T> responseType, Class<R> failureType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.restPostWithEntity(getDataRestServiceURL() + path, postEntity, responseType, failureType, expectedSuccessCode, otherExpectedStatusCodes);
    }

    protected <T> ResponseEntity<T> restPostWithEntity(String path, Object postEntity, Class<T> responseType) {
        return adaptor.restPostWithEntity(getDataRestServiceURL() + path, postEntity, responseType);
    }

    protected void restPut(String path) {
        adaptor.restPutWithEntity(getDataRestServiceURL() + path, Void.class);
    }

    protected <T> ResponseEntity<T> restPutEntity(String path, Class<T> c) {
        return adaptor.restPutWithEntity(getDataRestServiceURL() + path, c);
    }

    protected void restPut(String path, Object entity) {
        adaptor.restPut(getDataRestServiceURL() + path, entity);
    }

    protected <T> ResponseEntity<T> restPut(String path, Object entity, Class<T> c) {
        return adaptor.restPut(getDataRestServiceURL() + path, entity, c);
    }

    protected <T, R> Either<ResponseEntity<R>, ResponseEntity<T>> restPutWithEntity(String path, Object postEntity, Class<T> responseType, Class<R> failureType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.restPutWithEntity(getDataRestServiceURL() + path, postEntity, responseType, failureType, expectedSuccessCode, otherExpectedStatusCodes);
    }

    protected void restDelete(String path) {
        adaptor.restDelete(getDataRestServiceURL() + path);
    }

    // Asynchronous public calls
    protected <T> Future<RestResult<T>> getWithRestResultAsync(String path, Class<T> returnType) {
        return adaptor.getWithRestResultAsyc(getDataRestServiceURL() + path, returnType);
    }

    public <T> ListenableFuture<ResponseEntity<T>> restGetAsync(String path, Class<T> clazz) {
        return adaptor.restGetAsync(getDataRestServiceURL() + path, clazz);
    }

    protected HttpHeaders createFileUploadHeader(String contentType, long contentLength){
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(contentLength);
        headers.setAccept(singletonList(MediaType.parseMediaType("application/json")));
        return headers;
    }
}

