package com.worth.ifs.transactional;

/**
 * A class representing a successful service call.  Typically returned in an Either as its right part.
 *
 * Created by dwatson on 06/10/15.
 */
public class ServiceSuccess {

    private String message;

    public ServiceSuccess() {

    }

    public ServiceSuccess(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
