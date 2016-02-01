package com.worth.ifs.transactional;

import org.springframework.http.HttpStatus;

/**
 *
 */
public interface ErrorTemplate {

    String getErrorKey();
    String getErrorMessage();
    HttpStatus getCategory();

}
