package com.worth.ifs.commons.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.List;

import static com.worth.ifs.util.MapFunctions.getSortedGroupingCounts;
import static java.util.Collections.singletonList;

/**
 *
 */
public class RestErrorEnvelope {

    private static final Log LOG = LogFactory.getLog(RestErrorEnvelope.class);

    private List<Error> errors;

    /**
     * For JSON marshalling
     */
    public RestErrorEnvelope() {
    }

    public RestErrorEnvelope(Error error) {
        this(singletonList(error));
    }

    public RestErrorEnvelope(List<Error> errors) {
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }

    @JsonIgnore
    public HttpStatus getStatusCode() {

        LinkedHashMap<HttpStatus, Integer> entries = getHttpStatusCounts();
        return entries.entrySet().iterator().next().getKey();
    }

    private LinkedHashMap<HttpStatus, Integer> getHttpStatusCounts() {
        return getSortedGroupingCounts(errors, Error::getStatusCode);
    }

    public boolean is(ErrorTemplate errorTemplate, List<Object> arguments) {

        List<Error> expectedErrors = singletonList(new Error(errorTemplate, arguments));
        return errorListsMatch(expectedErrors);
    }

    public boolean is(Error error) {

        List<Error> expectedErrors = singletonList(error);
        return errorListsMatch(expectedErrors);
    }

    private boolean errorListsMatch(List<Error> expectedErrors) {

        if (expectedErrors.size() != errors.size()) {
            LOG.warn("Error lists don't match by size - expected " + expectedErrors + " but got " + errors);
            return false;
        }

        return errors.containsAll(expectedErrors);
    }
}
