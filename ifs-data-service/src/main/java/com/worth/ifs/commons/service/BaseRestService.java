package com.worth.ifs.commons.service;

import com.worth.ifs.commons.rest.RestResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Future;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

/**
 * BaseRestService provides a base for all Service classes.
 */
public abstract class BaseRestService {
    private final static Log LOG = LogFactory.getLog(BaseRestService.class);

    @Autowired
    private RestTemplateAdaptor adaptor;

    private String dataRestServiceURL;

    protected String getDataRestServiceURL() {
        return dataRestServiceURL;
    }

    @Value("${ifs.data.service.rest.baseURL}")
    public void setDataRestServiceUrl(String dataRestServiceURL) {
        this.dataRestServiceURL = dataRestServiceURL;
    }

    public <T> ListenableFuture<ResponseEntity<T>> restGetAsync(String path, Class<T> clazz) {
        return adaptor.restGetAsync(getDataRestServiceURL() + path, clazz);
    }

    protected <T> T restGet(String path, Class<T> c) {
        return restGetEntity(path, c).getBody();
    }

    protected <T> T restGet(String path, Class<T> c, HttpHeaders headers) {
        return restGetEntity(path, c, headers).getBody();
    }

    protected <T> RestResult<T> getWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, GET, returnType);
    }

    protected <T> RestResult<T> getWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, GET, returnType);
    }

    protected <T> RestResult<T> deleteWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, DELETE, returnType, OK, NO_CONTENT);
    }

    protected <T> RestResult<T> deleteWithRestResult(String path, Class<T> returnType) {
        return exchangeWithRestResult(path, DELETE, returnType, OK, NO_CONTENT);
    }

    protected <T> Future<RestResult<T>> getWithRestResultAsyc(String path, Class<T> returnType) {
        return exchangeWithRestResultAsync(path, GET, returnType);
    }

    protected <T> ResponseEntity<T> restGetEntity(String path, Class<T> c) {
        return adaptor.restGetEntity(getDataRestServiceURL() + path, c);
    }

    protected <T> ResponseEntity<T> restGetEntity(String path, Class<T> c, HttpHeaders headers) {
        return adaptor.restGetEntity(getDataRestServiceURL() + path, c, headers);
    }

    protected <T> ResponseEntity<T> restGet(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.restGet(getDataRestServiceURL() + path, returnType);
    }package com.worth.ifs.commons.service;

import com.worth.ifs.commons.rest.RestResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Future;

/**
 * BaseRestService provides a base for all Service classes.
 */
public abstract class BaseRestService {
    private final static Log LOG = LogFactory.getLog(BaseRestService.class);

    @Autowired
    private RestTemplateAdaptor adaptor;

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

    // Synchronous calls
    protected <T> RestResult<T> getWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.getWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <T> RestResult<T> getWithRestResult(String path, Class<T> returnType) {
        return adaptor.getWithRestResult(getDataRestServiceURL() + path, returnType);
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

    protected <T> RestResult<T> deleteWithRestResult(String path, ParameterizedTypeReference<T> returnType) {
        return adaptor.deleteWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <T> RestResult<T> deleteWithRestResult(String path, Class<T> returnType) {
        return adaptor.deleteWithRestResult(getDataRestServiceURL() + path, returnType);
    }

    protected <T> T restGet(String path, Class<T> c) {
        return restGetEntity(path, c).getBody();
    }

    protected <T> T restGet(String path, Class<T> c, HttpHeaders headers) {
        return restGetEntity(path, c, headers).getBody();
    }

    protected <T> ResponseEntity<T> restGetEntity(String path, Class<T> c) {
        return adaptor.restGetEntity(getDataRestServiceURL() + path, c);
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

    protected <T> ResponseEntity<T> restPostWithEntity(String path, Object postEntity, Class<T> responseType) {
        return adaptor.restPostWithEntity(getDataRestServiceURL() + path, postEntity, responseType);
    }

    protected void restPut(String path) {
        adaptor.restPutEntity(getDataRestServiceURL() + path, Void.class);
    }

    protected <T> ResponseEntity<T> restPutEntity(String path, Class<T> c) {
        return adaptor.restPutEntity(getDataRestServiceURL() + path, c);
    }

    protected void restPut(String path, Object entity) {
        adaptor.restPut(getDataRestServiceURL() + path, entity);
    }

    protected <T> ResponseEntity<T> restPut(String path, Object entity, Class<T> c) {
        return adaptor.restPut(getDataRestServiceURL() + path, entity, c);
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
}



    protected <T> T restPost(String path, Object postEntity, Class<T> c) {
        return adaptor.restPostWithEntity(getDataRestServiceURL() + path, postEntity, c).getBody();
    }

    protected void restPut(String path) {
        adaptor.restPutEntity(getDataRestServiceURL() + path, Void.class);
    }

    protected <T> ResponseEntity<T> restPutEntity(String path, Class<T> c) {
        return adaptor.restPutEntity(getDataRestServiceURL() + path, c);
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

    protected <R> RestResult<R> putWithRestResult(String path, Object objectToSend, ParameterizedTypeReference<R> returnType) {
        return exchangeObjectWithRestResult(path, PUT, objectToSend, returnType, OK);
    }

    protected <R> RestResult<R> putWithRestResult(String path, Object objectToSend, Class<R> returnType) {
        return exchangeObjectWithRestResult(path, PUT, objectToSend, returnType, OK);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, ParameterizedTypeReference<T> returnType) {
        return exchangeWithRestResult(path, method, returnType, OK);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, Class<T> returnType) {
        return exchangeWithRestResult(path, method, returnType, OK);
    }

    private <T> Future<RestResult<T>> exchangeWithRestResultAsync(String path, HttpMethod method, Class<T> returnType) {
        return exchangeWithRestResultAsync(path, method, returnType, OK);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, ParameterizedTypeReference<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.exchangeWithRestResult(getDataRestServiceURL() + path, method, returnType, expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeObjectWithRestResult(String path, HttpMethod method, Object objectToSend, ParameterizedTypeReference<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.exchangeObjectWithRestResult(getDataRestServiceURL() + path, method, objectToSend, returnType, expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeWithRestResult(String path, HttpMethod method, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.exchangeWithRestResult(getDataRestServiceURL() + path, method, returnType, expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> Future<RestResult<T>> exchangeWithRestResultAsync(String path, HttpMethod method, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.exchangeWithRestResultAsync(getDataRestServiceURL() + path, method, returnType, expectedSuccessCode, otherExpectedStatusCodes);
    }

    private <T> RestResult<T> exchangeObjectWithRestResult(String path, HttpMethod method, Object objectToSend, Class<T> returnType, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        return adaptor.exchangeObjectWithRestResult(getDataRestServiceURL() + path, method, objectToSend, returnType, expectedSuccessCode, otherExpectedStatusCodes);
    }

    protected void restPut(String path, Object entity) {
        adaptor.restPut(getDataRestServiceURL() + path, entity);
    }

    protected <T> ResponseEntity<T> restPut(String path, Object entity, Class<T> c) {
        return adaptor.restPut(getDataRestServiceURL() + path, entity, c);

    }

    protected void restDelete(String path) {
        adaptor.restDelete(getDataRestServiceURL() + path);
    }

    protected <T> ResponseEntity<T> restPostWithEntity(String path, Object postEntity, Class<T> responseType) {
        return adaptor.restPostWithEntity(getDataRestServiceURL() + path, postEntity, responseType);
    }

    public void setRestTemplateAdaptor(RestTemplateAdaptor adaptor) {
        this.adaptor = adaptor;
    }
}

