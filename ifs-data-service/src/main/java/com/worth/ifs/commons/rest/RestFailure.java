package com.worth.ifs.commons.rest;

import com.worth.ifs.commons.error.Error;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static com.worth.ifs.util.CollectionFunctions.simpleToMap;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.groupingBy;

/**
 * This class represents a failure encountered during a service call and can additionally contain 0 or more error
 * messages within it.
 */
public class RestFailure {

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

    public static RestFailure error(String key, String message, HttpStatus statusCode) {
        return new RestFailure(singletonList(new Error(key, message, statusCode)));
    }

    public boolean is(String... messages) {
        List<String> containedErrors = simpleMap(errors, Error::getErrorKey);
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

        List<Map.Entry<HttpStatus, Integer>> entries = getHttpStatusCounts();
        return entries.get(0).getKey();
    }

    private List<Map.Entry<HttpStatus, Integer>> getHttpStatusCounts() {

        Map<HttpStatus, List<Error>> errorsByStatusCode = errors.stream().collect(groupingBy(Error::getStatusCode));
        Map<HttpStatus, Integer> numberOfOccurrancesByStatusCode =
                simpleToMap(new ArrayList<>(errorsByStatusCode.entrySet()), Map.Entry::getKey, entry -> entry.getValue().size());

        List<Map.Entry<HttpStatus, Integer>> entries = new ArrayList<>(numberOfOccurrancesByStatusCode.entrySet());
        entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        return entries;
    }
}
