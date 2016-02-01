package com.worth.ifs.commons.controller;

import com.google.common.base.Supplier;
import com.worth.ifs.transactional.RestResult;
import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.function.Function;

import static com.worth.ifs.transactional.RestResult.restFailure;
import static com.worth.ifs.transactional.RestResults.internalServerError2;
import static com.worth.ifs.transactional.RestResults.ok;

/**
 *
 */
public class RestResultBuilder<T, R> {

    private static final Log LOG = LogFactory.getLog(RestResultBuilder.class);

    private static RestResult<?> defaultSuccessResult = ok();
    private static RestResult<?> fallbackFailureResult = internalServerError2();
    private Function<R, RestResult<T>> successResult;
    private RestResult<T> defaultFailureResult;
    private Supplier<ServiceResult<R>> serviceResult;

    private RestResultBuilder() {
    }

    private RestResultBuilder(RestResultBuilder<T, R> existingBuilder) {
        this.successResult = existingBuilder.successResult;
        this.defaultFailureResult = existingBuilder.defaultFailureResult;
        this.serviceResult = existingBuilder.serviceResult;
    }

    public static <T, R> RestResultBuilder<T, R> newRestResult(Class<T> returnClazz, Class<R> mainResultClazz) {
        return new RestResultBuilder<>();
    }

    public RestResultBuilder<T, R> andOnSuccess(RestResult<T> successResult) {
        return andOnSuccess(success -> successResult);
    }

    public RestResultBuilder<T, R> andOnSuccess(Function<R, RestResult<T>> successResult) {
        RestResultBuilder<T, R> newBuilder = new RestResultBuilder<>(this);
        newBuilder.successResult = successResult;
        return newBuilder;
    }

    public RestResultBuilder<T, R> andWithDefaultFailure(RestResult<T> failureResult) {
        RestResultBuilder<T, R> newBuilder = new RestResultBuilder<>(this);
        newBuilder.defaultFailureResult = failureResult;
        return newBuilder;
    }

    public RestResult<T> handleServiceResult(Supplier<ServiceResult<R>> serviceResult) {
        RestResultBuilder<T, R> newBuilder = new RestResultBuilder<>(this);
        newBuilder.serviceResult = serviceResult;
        return newBuilder.perform();
    }

    public RestResult<T> perform() {

        if (serviceResult != null) {

            try {
                ServiceResult<R> response = serviceResult.get();
                return response.mapLeftOrRight(failure -> {

                    RestResult<T> handled = handleServiceFailure(failure);

                    if (handled != null) {
                        return handled;
                    }

                    if (defaultFailureResult != null) {
                        return defaultFailureResult;
                    } else {
                        return (RestResult<T>) fallbackFailureResult;
                    }
                }, success -> {
                    if (successResult != null) {
                        return successResult.apply(success);
                    } else {
                        return (RestResult<T>) defaultSuccessResult;
                    }
                });

            } catch (Exception e) {
                LOG.warn("Uncaught exception encountered while performing RestResult processing - returning catch-all error", e);
                return (RestResult<T>) fallbackFailureResult;
            }
        }

        return null;
    }

    private RestResult<T> handleServiceFailure(ServiceFailure failure) {
        return restFailure(failure.getErrors());
    }
}
