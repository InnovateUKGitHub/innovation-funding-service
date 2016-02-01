package com.worth.ifs.commons.controller;

import com.worth.ifs.transactional.*;
import com.worth.ifs.transactional.Error;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;

import java.util.function.Supplier;

import static com.worth.ifs.transactional.BaseTransactionalService.Failures.UNEXPECTED_ERROR;
import static com.worth.ifs.transactional.RestResult.restFailure;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

/**
 *
 */
public abstract class AbstractDataController {

    private static final Log LOG = LogFactory.getLog(AbstractDataController.class);

    protected <T> RestResult<T> serviceToRestResult(Supplier<ServiceResult<T>> serviceCode) {
        return serviceToRestResult(serviceCode, getDefaultSuccessHandler());
    }

    protected <T> RestResult<T> serviceToRestResult(Supplier<ServiceResult<T>> serviceCode, HttpStatus successStatusCode) {
        return serviceToRestResult(serviceCode, getDefaultSuccessHandler(successStatusCode));
    }

    protected <T, R> RestResult<R> serviceToRestResult(Supplier<ServiceResult<T>> serviceCode, RestResult<R> successResult) {
        return serviceToRestResult(serviceCode, getServiceResultSuccessToSuccessfulRestResultHandler(successResult));
    }

    protected <T, R> RestResult<R> serviceToRestResult(Supplier<ServiceResult<T>> serviceCode, ServiceSuccessToRestResultHandler<T, R> successHandler) {
        return serviceToRestResult(serviceCode, successHandler, (ServiceFailureToRestResultHandlers<T, R>) getDefaultFailureHandlers());
    }

    protected <T, R> RestResult<R> serviceToRestResult(Supplier<ServiceResult<T>> serviceCode, ServiceSuccessToRestResultHandler<T, R> successHandler, ServiceFailureToRestResultHandlers<T, R> failureHandlers) {
        return serviceToRestResult(serviceCode, successHandler, failureHandlers, getFallbackFailure());
    }

    protected <T, R> RestResult<R> serviceToRestResult(Supplier<ServiceResult<T>> serviceCode, ServiceSuccessToRestResultHandler<T, R> successHandler, ServiceFailureToRestResultHandlers<T, R> failureHandlers, RestFailure defaultFailure) {
        try {
            ServiceResult<T> response = serviceCode.get();
            return successHandler.apply(response).orElse(failureHandlers.apply(response).orElse(restFailure(defaultFailure)));

        } catch (Exception e) {
            LOG.warn("Uncaught exception encountered while performing Controller call - returning catch-all error", e);
            return restFailure(defaultFailure);
        }
    }

    private <T> ServiceSuccessToRestResultHandler<T, T> getDefaultSuccessHandler() {
        return getDefaultSuccessHandler(OK);
    }

    private <T> ServiceSuccessToRestResultHandler<T, T> getDefaultSuccessHandler(HttpStatus statusCode) {
        return new ServiceSuccessToHttpStatusCodeRestResultHandler<>(statusCode);
    }

    private <T> ServiceFailureToRestResultHandlers<T, T> getDefaultFailureHandlers() {
        return new ServiceFailureToRestResultHandlers<>(emptyList());
    }

    private RestFailure getFallbackFailure() {
        return new RestFailure(singletonList(new Error(UNEXPECTED_ERROR, "An unexpected error occurred", INTERNAL_SERVER_ERROR)));
    }

    private <T, R> ServiceSuccessToRestResultHandler<T, R> getServiceResultSuccessToSuccessfulRestResultHandler(RestResult<R> successResult) {
        return serviceResult -> serviceResult.isRight() ? of(successResult) : empty();
    }
}
