package com.worth.ifs;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

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
