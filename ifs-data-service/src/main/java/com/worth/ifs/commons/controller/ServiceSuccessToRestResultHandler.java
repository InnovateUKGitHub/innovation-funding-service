package com.worth.ifs.commons.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ServiceResult;

import java.util.Optional;
import java.util.function.Function;

/**
 *
 */
@FunctionalInterface
public interface ServiceSuccessToRestResultHandler<T, R> extends Function<ServiceResult<T>, Optional<RestResult<R>>> {

    @Override
    Optional<RestResult<R>> apply(ServiceResult<T> serviceResult);
}
