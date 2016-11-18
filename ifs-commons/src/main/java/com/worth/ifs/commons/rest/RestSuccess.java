package com.worth.ifs.commons.rest;

import org.springframework.http.HttpStatus;

/**
 * Represents a RestResult success case, including the main response object of the Rest Controller method, and the appropriate
 * success HTTP status code
 */
public class RestSuccess<T> {

    private T result;
    private HttpStatus statusCode;

    /**
     * For JSON marshalling
     */
    public RestSuccess() {
    	// no-arg constructor
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
