package com.worth.ifs.commons.controller;

import com.worth.ifs.transactional.*;
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

    protected <T> RestResult<T> serviceToRestResult(Supplier<ServiceResult<T>> serviceCode, RestResult<T> successResult) {
        return serviceToRestResult(serviceCode, getServiceResultSuccessToSuccessfulRestResultHandler(successResult));
    }

    protected <T> RestResult<T> serviceToRestResult(Supplier<ServiceResult<T>> serviceCode, ServiceSuccessToRestResultHandler<T> successHandler) {
        return serviceToRestResult(serviceCode, successHandler, getDefaultFailureHandlers());
    }

    protected <T> RestResult<T> serviceToRestResult(Supplier<ServiceResult<T>> serviceCode, ServiceSuccessToRestResultHandler<T> successHandler, ServiceFailureToRestResultHandlers<T> failureHandlers) {
        return serviceToRestResult(serviceCode, successHandler, failureHandlers, getFallbackFailure());
    }

    protected <T> RestResult<T> serviceToRestResult(Supplier<ServiceResult<T>> serviceCode, ServiceSuccessToRestResultHandler<T> successHandler, ServiceFailureToRestResultHandlers<T> failureHandlers, RestFailure defaultFailure) {
        try {
            ServiceResult<T> response = serviceCode.get();
            return successHandler.apply(response).orElse(failureHandlers.apply(response).orElse(restFailure(defaultFailure)));

        } catch (Exception e) {
            LOG.warn("Uncaught exception encountered while performing Controller call - returning catch-all error", e);
            return restFailure(defaultFailure);
        }
    }

    private <T> ServiceSuccessToRestResultHandler<T> getDefaultSuccessHandler() {
        return getDefaultSuccessHandler(OK);
    }

    private <T> ServiceSuccessToRestResultHandler<T> getDefaultSuccessHandler(HttpStatus statusCode) {
        return new ServiceSuccessToHttpStatusCodeRestResultHandler<>(statusCode);
    }

    private <T> ServiceFailureToRestResultHandlers<T> getDefaultFailureHandlers() {
        return new ServiceFailureToRestResultHandlers<>(emptyList());
    }

    private RestFailure getFallbackFailure() {
        return new RestFailure(singletonList(new RestError(UNEXPECTED_ERROR, "An unexpected error occurred", INTERNAL_SERVER_ERROR)));
    }

    private <T> ServiceSuccessToRestResultHandler<T> getServiceResultSuccessToSuccessfulRestResultHandler(RestResult<T> successResult) {
        return serviceResult -> serviceResult.isRight() ? of(successResult) : empty();
    }
}
