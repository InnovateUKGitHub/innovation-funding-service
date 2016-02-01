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
public class RestResultBuilder<ProcessResultType, ReturnType> {

    private static final Log LOG = LogFactory.getLog(RestResultBuilder.class);

    private static RestResult<?> defaultSuccessResult = ok();
    private static RestResult<?> fallbackFailureResult = internalServerError2();
    private Function<ProcessResultType, RestResult<ReturnType>> successResult;
    private RestResult<ReturnType> defaultFailureResult;
    private Supplier<ServiceResult<ProcessResultType>> serviceResult;

    private RestResultBuilder() {
    }

    private RestResultBuilder(RestResultBuilder<ProcessResultType, ReturnType> existingBuilder) {
        this.successResult = existingBuilder.successResult;
        this.defaultFailureResult = existingBuilder.defaultFailureResult;
        this.serviceResult = existingBuilder.serviceResult;
    }

    public static <T, R> RestResultBuilder<R, T> newRestResult(Class<R> mainResultClazz, Class<T> returnClazz) {
        return new RestResultBuilder<>();
    }

    public RestResultBuilder<ProcessResultType, ReturnType> andOnSuccess(RestResult<ReturnType> successResult) {
        return andOnSuccess(success -> successResult);
    }

    public RestResultBuilder<ProcessResultType, ReturnType> andOnSuccess(Function<ProcessResultType, RestResult<ReturnType>> successResult) {
        RestResultBuilder<ProcessResultType, ReturnType> newBuilder = new RestResultBuilder<>(this);
        newBuilder.successResult = successResult;
        return newBuilder;
    }

    public RestResultBuilder<ProcessResultType, ReturnType> andWithDefaultFailure(RestResult<ReturnType> failureResult) {
        RestResultBuilder<ProcessResultType, ReturnType> newBuilder = new RestResultBuilder<>(this);
        newBuilder.defaultFailureResult = failureResult;
        return newBuilder;
    }

    public RestResult<ReturnType> perform(Supplier<ServiceResult<ProcessResultType>> serviceResult) {
        RestResultBuilder<ProcessResultType, ReturnType> newBuilder = new RestResultBuilder<>(this);
        newBuilder.serviceResult = serviceResult;
        return newBuilder.perform();
    }

    private RestResult<ReturnType> perform() {

        if (serviceResult != null) {

            try {
                ServiceResult<ProcessResultType> response = serviceResult.get();
                return response.mapLeftOrRight(failure -> {

                    RestResult<ReturnType> handled = handleServiceFailure(failure);

                    if (handled != null) {
                        return handled;
                    }

                    if (defaultFailureResult != null) {
                        return defaultFailureResult;
                    } else {
                        return (RestResult<ReturnType>) fallbackFailureResult;
                    }
                }, success -> {
                    if (successResult != null) {
                        return successResult.apply(success);
                    } else {
                        return (RestResult<ReturnType>) defaultSuccessResult;
                    }
                });

            } catch (Exception e) {
                LOG.warn("Uncaught exception encountered while performing RestResult processing - returning catch-all error", e);
                return (RestResult<ReturnType>) fallbackFailureResult;
            }
        }

        return null;
    }

    private RestResult<ReturnType> handleServiceFailure(ServiceFailure failure) {
        return restFailure(failure.getErrors());
    }
}
