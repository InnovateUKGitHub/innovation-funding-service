package com.worth.ifs.transactional;

import org.springframework.http.HttpStatus;

import java.util.List;

/**
 *
 */
public class Errors {

    public Error notFound(String entity, List<Object> arguments) {
        return new Error(HttpStatus.NOT_FOUND, entity + " not found", arguments, HttpStatus.NOT_FOUND);
    }
}
