package com.worth.ifs.commons.error;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

/**
 * Represents a template from which an Error can be created, along with case-specific arguments that are not a part of this
 * template.
 */
public interface ErrorTemplate extends Serializable {

    String getErrorKey();
    String getErrorMessage();
    HttpStatus getCategory();

}
