package com.worth.ifs.transactional;

/**
 * A class representing a successful service call.  Typically returned in an Either as its right part.
 *
 * Created by dwatson on 06/10/15.
 */
public class ServiceSuccess<T> {

    private String message;
    private T result;

    public ServiceSuccess() {

    }


    public ServiceSuccess(T result) {
        this.result = result;
    }

    public ServiceSuccess(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public T getResult() {
        return result;
    }
}
