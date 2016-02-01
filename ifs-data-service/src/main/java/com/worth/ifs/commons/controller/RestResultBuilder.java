package com.worth.ifs.commons.controller;

import com.google.common.base.Supplier;
import com.worth.ifs.transactional.RestResult;
import com.worth.ifs.transactional.ServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class RestResultBuilder<T, R> {

    private static final Log LOG = LogFactory.getLog(RestResultBuilder.class);

    private RestResult<T> successResult;
    private RestResult<T> failureResult;
    private Supplier<ServiceResult<R>> serviceResult;

    private RestResultBuilder() {
    }

    private RestResultBuilder(RestResultBuilder<T, R> existingBuilder) {
        this.successResult = existingBuilder.successResult;
        this.failureResult = existingBuilder.failureResult;
        this.serviceResult = existingBuilder.serviceResult;
    }

    public static <T, R> RestResultBuilder<T, R> newRestResult(Class<T> returnClazz, Class<R> mainResultClazz) {
        return new RestResultBuilder<>();
    }

    public RestResultBuilder<T, R> andOnSuccess(RestResult<T> successResult) {
        RestResultBuilder<T, R> newBuilder = new RestResultBuilder<>(this);
        newBuilder.successResult = successResult;
        return newBuilder;
    }

    public RestResultBuilder<T, R> andWithDefaultFailure(RestResult<T> failureResult) {
        RestResultBuilder<T, R> newBuilder = new RestResultBuilder<>(this);
        newBuilder.failureResult = failureResult;
        return newBuilder;
    }

    public RestResultBuilder<T, R> handlingServiceResult(Supplier<ServiceResult<R>> serviceResult) {
        RestResultBuilder<T, R> newBuilder = new RestResultBuilder<>(this);
        newBuilder.serviceResult = serviceResult;
        return newBuilder;
    }

    public RestResult<T> perform() {

        if (serviceResult != null) {

            try {
                ServiceResult<R> response = serviceResult.get();
                return response.mapLeftOrRight(failure -> failureResult, success -> successResult);

            } catch (Exception e) {
                LOG.warn("Uncaught exception encountered while performing RestResult processing - returning catch-all error", e);
                return failureResult;
            }
        }

        return null;
    }
}
