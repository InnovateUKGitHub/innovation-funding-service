package com.worth.ifs.commons.rest;

import org.springframework.http.HttpStatus;

/**
 *
 */
public class RestSuccess<T> {

    private T result;
    private HttpStatus statusCode;

    /**
     * For JSON marshalling
     */
    public RestSuccess() {
    }

    public RestSuccess(T result, HttpStatus statusCode) {
        this.result = result;
        this.statusCode = statusCode;
    }

    public T getResult() {
        return result;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
