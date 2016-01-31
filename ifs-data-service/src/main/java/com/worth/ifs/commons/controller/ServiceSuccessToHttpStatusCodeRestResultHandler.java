package com.worth.ifs.commons.controller;

import com.worth.ifs.transactional.RestResult;
import com.worth.ifs.transactional.ServiceResult;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static com.worth.ifs.transactional.RestResult.restSuccess;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 *
 */
public class ServiceSuccessToHttpStatusCodeRestResultHandler<T> implements ServiceSuccessToRestResultHandler<T> {

    private HttpStatus statusCode;

    public ServiceSuccessToHttpStatusCodeRestResultHandler(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public Optional<RestResult<T>> apply(ServiceResult<T> serviceResult) {

        if (serviceResult.isLeft()) {
            return empty();
        }

        return of(restSuccess(serviceResult.getRight(), statusCode));
    }
}
