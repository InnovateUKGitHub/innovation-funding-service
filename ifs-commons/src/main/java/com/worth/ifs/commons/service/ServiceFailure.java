package com.worth.ifs.commons.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorHolder;
import com.worth.ifs.commons.error.ErrorTemplate;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;

/**
 * This class represents a failure encountered during a service call and can additionally contain 0 or more error
 * messages within it.
 */
public class ServiceFailure implements ErrorHolder {

    private List<Error> errors;
    private Throwable cause;

    ServiceFailure(List<Error> errors) {
        this(errors, null);
    }

    ServiceFailure(List<Error> errors, Throwable e) {
        this.errors = errors;
        this.cause = e;
    }

    public boolean is(Error... expectedErrors) {
        List<Error> expectedErrorsList = asList(expectedErrors);
        return this.errors.size() == expectedErrorsList.size() && errors.containsAll(expectedErrorsList);
    }

    public boolean is(ErrorTemplate... expectedErrorTemplates) {
        List<Error> errorList = simpleMap(asList(expectedErrorTemplates), Error::new);
        return is(errorList.toArray(new Error[errorList.size()]));
    }

    public boolean is(ErrorTemplate expectedErrorTemplate, Object... arguments) {
        return is(new Error(expectedErrorTemplate, arguments));
    }

    public List<Error> getErrors() {
        return errors;
    }

    public Throwable getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("errors", errors)
                .append("cause", cause)
                .toString();
    }
}
