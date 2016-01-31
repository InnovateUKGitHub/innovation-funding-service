package com.worth.ifs.commons.controller;

import com.worth.ifs.transactional.RestResult;
import com.worth.ifs.transactional.ServiceResult;

import java.util.Optional;
import java.util.function.Function;

/**
 *
 */
@FunctionalInterface
public interface ServiceSuccessToRestResultHandler<T> extends Function<ServiceResult<T>, Optional<RestResult<T>>> {

    @Override
    Optional<RestResult<T>> apply(ServiceResult<T> serviceResult);
}
