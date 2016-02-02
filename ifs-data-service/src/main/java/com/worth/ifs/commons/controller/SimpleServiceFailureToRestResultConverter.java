package com.worth.ifs.commons.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceFailure;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Optional.empty;

/**
 * A simple implementation of ServiceFailureToJsonResponseHandler that, given a set of supported error messages, is able to
 * inspect a ServiceFailure and, if this handler handles all of its errors, will return an appropriate JsonStatusResponse.
 */
public class SimpleServiceFailureToRestResultConverter implements ServiceFailureToRestResultConverter {

    private List<String> handledServiceFailures;
    private Function<ServiceFailure, RestResult<?>> handlerFunction;

    public SimpleServiceFailureToRestResultConverter(Function<ServiceFailure, RestResult<?>> handlerFunction, List<String> handledServiceFailures) {
        this.handledServiceFailures = handledServiceFailures;
        this.handlerFunction = handlerFunction;
    }

    public SimpleServiceFailureToRestResultConverter(List<Enum<?>> handledServiceFailures, Function<ServiceFailure, RestResult<?>> handlerFunction) {
        this(handlerFunction, simpleMap(handledServiceFailures, Enum::name));
    }

    @Override
    public Optional<RestResult<?>> handle(ServiceFailure serviceFailure) {

        if (handledServiceFailures.containsAll(serviceFailure.getErrorKeys())) {
            return Optional.of(handlerFunction.apply(serviceFailure));
        }

        return empty();
    }
}
