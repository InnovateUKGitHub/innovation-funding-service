package com.worth.ifs.commons.controller;

import com.worth.ifs.transactional.RestResult;
import com.worth.ifs.transactional.ServiceResult;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 *
 */
public class ServiceFailureToRestResultHandlers<T> implements Function<ServiceResult<T>, Optional<RestResult<T>>> {

    private List<ServiceFailureToRestResultConverter> handlers;

    public ServiceFailureToRestResultHandlers(List<ServiceFailureToRestResultConverter> handlers) {
        this.handlers = handlers;
    }

    @Override
    public Optional<RestResult<T>> apply(ServiceResult<T> serviceResult) {

        if (serviceResult.isRight()) {
            return empty();
        }

        for (ServiceFailureToRestResultConverter handler : handlers) {

            Optional<RestResult<?>> result = handler.handle(serviceResult.getLeft());

            if (result.isPresent()) {
                RestResult<T> restResult = (RestResult<T>) result.get();
                return of(restResult);
            }
        }

        return empty();
    }
}
