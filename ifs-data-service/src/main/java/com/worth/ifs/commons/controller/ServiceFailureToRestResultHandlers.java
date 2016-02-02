package com.worth.ifs.commons.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 *
 */
public class ServiceFailureToRestResultHandlers<T, R> implements Function<ServiceResult<T>, Optional<RestResult<R>>> {

    private List<ServiceFailureToRestResultConverter> handlers;

    public ServiceFailureToRestResultHandlers(List<ServiceFailureToRestResultConverter> handlers) {
        this.handlers = handlers;
    }

    @Override
    public Optional<RestResult<R>> apply(ServiceResult<T> serviceResult) {

        if (serviceResult.isRight()) {
            return empty();
        }

        for (ServiceFailureToRestResultConverter handler : handlers) {

            Optional<RestResult<?>> result = handler.handle(serviceResult.getLeft());

            if (result.isPresent()) {
                RestResult<R> restResult = (RestResult<R>) result.get();
                return of(restResult);
            }
        }

        return empty();
    }
}
