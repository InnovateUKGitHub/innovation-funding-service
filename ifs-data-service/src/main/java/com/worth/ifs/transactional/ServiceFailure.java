package com.worth.ifs.transactional;

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

    private ServiceFailure(List<Error> errors, Throwable e) {
        this.errors = errors;
        this.cause = e;
    }

    public static ServiceFailure error(List<String> messages) {
        return new ServiceFailure(simpleMap(messages, Error::new), null);
    }

    public static ServiceFailure error(List<String> messages, Throwable e) {
        return new ServiceFailure(simpleMap(messages, Error::new), e);
    }

    public static ServiceFailure error(String... messages) {
        return error(asList(messages));
    }

    public static ServiceFailure error(String message) {
        return error(new String[] { message });
    }

    public static ServiceFailure error(Enum<?> message) {
        return error(message.name());
    }

    public static ServiceFailure error(Enum<?>... messages) {
        return error(simpleMap(asList(messages), Enum::name));
    }

    public static ServiceFailure error(Throwable e, String... messages) {
        return error(asList(messages), e);
    }

    public static ServiceFailure error(String message, Throwable e) {
        return error(e, message);
    }

    public static ServiceFailure error(Enum<?> message, Throwable e) {
        return error(message.name(), e);
    }

    public static ServiceFailure error(Throwable e, Enum<?>... messages) {
        return error(simpleMap(asList(messages), Enum::name), e);
    }

    public boolean is(String... messages) {
        List<String> containedErrors = getErrorKeys();
        List<String> messagesList = asList(messages);
        return containedErrors.containsAll(messagesList) && messagesList.containsAll(containedErrors);
    }

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
