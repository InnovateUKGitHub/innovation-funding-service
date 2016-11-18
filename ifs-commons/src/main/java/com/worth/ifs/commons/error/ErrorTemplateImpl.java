package com.worth.ifs.commons.error;

import org.springframework.http.HttpStatus;

/**
 * Represents a template from which an Error can be created, along with case-specific arguments that are not a part of this
 * template.
 */
public class ErrorTemplateImpl implements ErrorTemplate {

    private String errorKey;
    private HttpStatus category;

    public ErrorTemplateImpl(String errorKey, HttpStatus category) {
        this.errorKey = errorKey;
        this.category = category;
    }

    @Override
    public String getErrorKey() {
        return errorKey;
    }

    @Override
    public HttpStatus getCategory() {
        return category;
    }
}
