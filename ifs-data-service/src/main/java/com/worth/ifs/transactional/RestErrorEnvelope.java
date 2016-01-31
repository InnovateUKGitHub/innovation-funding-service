package com.worth.ifs.transactional;

import java.util.List;

/**
 *
 */
public class RestErrorEnvelope {

    private List<Error> errors;

    /**
     * For JSON marshalling
     */
    public RestErrorEnvelope() {
    }

    public RestErrorEnvelope(List<Error> errors) {
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }
}
