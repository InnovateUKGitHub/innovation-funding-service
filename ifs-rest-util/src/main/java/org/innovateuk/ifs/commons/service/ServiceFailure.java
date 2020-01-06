package org.innovateuk.ifs.commons.service;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ErrorHolder;
import org.innovateuk.ifs.commons.error.ErrorTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;

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

    public String toDisplayString() {
        return (getCause() == null ? "" : getCause().getMessage() + " ") +
                getErrors().stream().map(Error::getDisplayString).collect(Collectors.joining(","));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("errors", errors)
                .append("cause", cause)
                .toString();
    }
}
