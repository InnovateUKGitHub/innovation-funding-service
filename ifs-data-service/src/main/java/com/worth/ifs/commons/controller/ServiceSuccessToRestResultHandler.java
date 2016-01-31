package com.worth.ifs.commons.controller;

import com.worth.ifs.transactional.RestResult;
import com.worth.ifs.transactional.ServiceResult;

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
