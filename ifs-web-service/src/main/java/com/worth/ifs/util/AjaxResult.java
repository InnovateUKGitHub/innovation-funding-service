package com.worth.ifs.util;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * Class to send results in a structured way, to the client (the browser). This is only used for sending results of a AJAX request.
 */
public class AjaxResult implements Serializable {


    HttpStatus status;
    String body;

    public AjaxResult(HttpStatus status, String body) {
        this.status = status;
        this.body = body;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
