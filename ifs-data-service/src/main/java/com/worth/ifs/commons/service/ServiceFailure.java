package com.worth.ifs.commons.service;

import com.worth.ifs.commons.error.Error;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;

/**
 * This class represents a failure encountered during a service call and can additionally contain 0 or more error
 * messages within it.
 */
public class ServiceFailure {

    private List<Error> errors;
    private Throwable cause;

    ServiceFailure(List<Error> errors) {
        this(errors, null);
    }

    ServiceFailure(List<Error> errors, Throwable e) {
        this.errors = errors;
        this.cause = e;
    }

    public boolean is(String... messages) {
        List<String> containedErrors = getErrorKeys();
        List<String> messagesList = asList(messages);
        return containedErrors.containsAll(messagesList) && messagesList.containsAll(containedErrors);
    }

    // TODO DW - INFUND-854 - not enough to check just keys
    public boolean is(Enum<?>... messages) {
        List<String> var = simpleMap(asList(messages), Enum::name);
        return is(var.toArray(new String[var.size()]));
    }

    public boolean contains(Enum<?>... messages) {
        List<String> messagesToCheck = simpleMap(asList(messages), Enum::name);
        return contains(messagesToCheck.toArray(new String[messagesToCheck.size()]));
    }

    public boolean contains(String... messages) {
        return getErrorKeys().containsAll(asList(messages));
    }

    public List<String> getErrorKeys() {
        return simpleMap(errors, Error::getErrorKey);
    }

    public List<Error> getErrors() {
        return errors;
    }

    public Throwable getCause() {
        return cause;
    }
}
