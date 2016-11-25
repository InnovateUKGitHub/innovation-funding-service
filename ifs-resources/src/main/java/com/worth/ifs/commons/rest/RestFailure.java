
package com.worth.ifs.commons.rest;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorHolder;
import com.worth.ifs.commons.error.ErrorTemplate;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.MapFunctions.getSortedGroupingCounts;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * This class represents a failure encountered during a service call and can additionally contain 0 or more error
 * messages within it.
 */
public class RestFailure implements ErrorHolder {

    private List<Error> errors;
    private HttpStatus specificStatusCode;

    public RestFailure(List<Error> errors) {
        this.errors = errors;
    }

    public RestFailure(List<Error> errors, HttpStatus specificStatusCode) {
        this(errors);
        this.specificStatusCode = specificStatusCode;
    }

    public static RestFailure error(String message, HttpStatus statusCode) {
        return new RestFailure(singletonList(new Error(message, statusCode)));
    }

    public static RestFailure error(List<Error> errors, HttpStatus statusCode) {
        return new RestFailure(errors, statusCode);
    }

    public static RestFailure error(List<Error> errors) {
        return new RestFailure(errors);
    }

    public boolean is(Error... expectedErrors) {
        List<Error> expectedErrorsList = asList(expectedErrors);
        return this.errors.size() == expectedErrorsList.size() && errors.containsAll(expectedErrorsList);
    }

    public boolean has(Error... expectedErrors) {
        List<Error> expectedErrorsList = asList(expectedErrors);
        return !expectedErrorsList.isEmpty() && this.errors.size() >= expectedErrorsList.size() && hasMatchingErrorKey(errors, expectedErrorsList.get(0));
    }

    public boolean is(ErrorTemplate... expectedErrorTemplates) {
        List<Error> errorList = simpleMap(asList(expectedErrorTemplates), Error::new);
        return is(errorList.toArray(new Error[errorList.size()]));
    }

    public boolean has(ErrorTemplate... expectedErrorTemplates) {
        List<Error> errorList = simpleMap(asList(expectedErrorTemplates), Error::new);
        return has(errorList.toArray(new Error[errorList.size()]));
    }

    public boolean is(ErrorTemplate expectedErrorTemplate, Object... arguments) {
        return is(new Error(expectedErrorTemplate, arguments));
    }

    public boolean contains(String... messages) {
        List<String> containedErrors = simpleMap(errors, Error::getErrorKey);
        return containedErrors.containsAll(asList(messages));
    }

    public List<Error> getErrors() {
        return errors;
    }

    public HttpStatus getStatusCode() {

        if (specificStatusCode != null) {
            return specificStatusCode;
        }

        LinkedHashMap<HttpStatus, Integer> entries = getHttpStatusCounts();
        return entries.entrySet().iterator().next().getKey();
    }

    private LinkedHashMap<HttpStatus, Integer> getHttpStatusCounts() {
        return getSortedGroupingCounts(errors, Error::getStatusCode);
    }

    private boolean hasMatchingErrorKey(final List<Error> errors, final Error expectedError){
        return errors.stream().anyMatch(e -> e.getErrorKey().equals(expectedError.getErrorKey()));
    }
}
