package com.worth.ifs.commons.controller;

import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.util.JsonStatusResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Optional.empty;

/**
 * A simple implementation of ServiceFailureToJsonResponseHandler that, given a set of supported error messages, is able to
 * inspect a ServiceFailure and, if this handler handles all of its errors, will return an appropriate JsonStatusResponse.
 */
public class SimpleServiceFailureToJsonResponseHandler implements ServiceFailureToJsonResponseHandler {

    private List<String> handledServiceFailures;
    private BiFunction<ServiceFailure, HttpServletResponse, JsonStatusResponse> handlerFunction;

    public SimpleServiceFailureToJsonResponseHandler(BiFunction<ServiceFailure, HttpServletResponse, JsonStatusResponse> handlerFunction, List<String> handledServiceFailures) {
        this.handledServiceFailures = handledServiceFailures;
        this.handlerFunction = handlerFunction;
    }

    public SimpleServiceFailureToJsonResponseHandler(List<Enum<?>> handledServiceFailures, BiFunction<ServiceFailure, HttpServletResponse, JsonStatusResponse> handlerFunction) {
        this(handlerFunction, simpleMap(handledServiceFailures, Enum::name));
    }

    @Override
    public Optional<JsonStatusResponse> handle(ServiceFailure serviceFailure, HttpServletResponse response) {

        if (handledServiceFailures.containsAll(serviceFailure.getErrors())) {
            return Optional.of(handlerFunction.apply(serviceFailure, response));
        }
        return empty();
    }
}
