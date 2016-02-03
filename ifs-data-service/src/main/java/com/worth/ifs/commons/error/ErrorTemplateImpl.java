package com.worth.ifs.commons.error;

import org.springframework.http.HttpStatus;

/**
 *
 */
public class ErrorTemplateImpl implements ErrorTemplate {

    private String errorKey;
    private String errorMessage;
    private HttpStatus category;

    public ErrorTemplateImpl(String errorKey, String errorMessage, HttpStatus category) {
        this.errorKey = errorKey;
        this.errorMessage = errorMessage;
        this.category = category;
    }

    @Override
    public String getErrorKey() {
        return errorKey;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public HttpStatus getCategory() {
        return category;
    }
}
