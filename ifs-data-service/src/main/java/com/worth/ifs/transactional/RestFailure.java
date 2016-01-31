package com.worth.ifs.transactional;

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

    private List<RestError> errors;

    public RestFailure(List<RestError> errors) {
        this.errors = errors;
    }

    public static RestFailure error(String message, HttpStatus statusCode) {
        return new RestFailure(singletonList(new RestError(new Error(message), statusCode)));
    }

    public boolean is(String... messages) {
        List<String> containedErrors = simpleMap(errors, error -> error.getError().getErrorMessage());
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
        List<String> containedErrors = simpleMap(errors, error -> error.getError().getErrorMessage());
        return containedErrors.containsAll(asList(messages));
    }

    public List<RestError> getErrors() {
        return errors;
    }

    public HttpStatus getStatusCode() {

        List<Map.Entry<HttpStatus, Integer>> entries = getHttpStatusCounts();
        return entries.get(0).getKey();
    }

    private List<Map.Entry<HttpStatus, Integer>> getHttpStatusCounts() {

        Map<HttpStatus, List<RestError>> errorsByStatusCode = errors.stream().collect(groupingBy(RestError::getStatusCode));
        Map<HttpStatus, Integer> numberOfOccurrancesByStatusCode =
                simpleToMap(new ArrayList<>(errorsByStatusCode.entrySet()), entry -> entry.getKey(), entry -> entry.getValue().size());

        List<Map.Entry<HttpStatus, Integer>> entries = new ArrayList<>(numberOfOccurrancesByStatusCode.entrySet());
        entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        return entries;
    }
}
