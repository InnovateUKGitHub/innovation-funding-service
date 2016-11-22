package com.worth.ifs.commons.error;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * Represents a template from which an Error can be created, along with case-specific arguments that are not a part of this
 * template.
 */
public interface ErrorTemplate extends Serializable {

    String getErrorKey();
    HttpStatus getCategory();

}
