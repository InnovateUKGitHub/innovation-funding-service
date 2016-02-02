package com.worth.ifs.commons.error;

import org.springframework.http.HttpStatus;

/**
 *
 */
public interface ErrorTemplate {

    String getErrorKey();
    String getErrorMessage();
    HttpStatus getCategory();

}
