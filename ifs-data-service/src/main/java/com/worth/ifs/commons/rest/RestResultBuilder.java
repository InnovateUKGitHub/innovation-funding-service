package com.worth.ifs.commons.rest;

import com.google.common.base.Supplier;
import com.worth.ifs.commons.service.ServiceFailure;
import com.worth.ifs.commons.service.ServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ParameterizedTypeReference;

import java.util.function.Function;

import static com.worth.ifs.commons.rest.RestFailures.internalServerErrorRestFailure;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.springframework.http.HttpStatus.OK;

/**
 * A builder that allows for a consistent way in which a Rest Controller method can perform some process and then act on
 * the results in a consistent, appropriate manner.  This includes consistent exception handling, and success or failure
 * case handling.
 *
 * It also provides handy mechanisms for integrating directly with ServiceResults being returned from the Service layer.
 */
public class RestResultBuilder<ProcessResultType, ReturnType> {

    private static final Log LOG = LogFactory.getLog(RestResultBuilder.class);

    private static RestResult<?> fallbackFailureResult = internalServerErrorRestFailure();
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

    public static <S> RestResultBuilder<S, S> newRestHandler(Class<S> clazz) {
        return newRestHandler();
    }

    public static <S> RestResultBuilder<S, S> newRestHandler(ParameterizedTypeReference<S> type) {
        return newRestHandler();
    }

    public static <T, R> RestResultBuilder<R, T> newRestHandler() {
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

    @SuppressWarnings("unchecked")
    public RestResultBuilder<ProcessResultType, ReturnType> andWithDefaultFailure(RestResult<?> failureResult) {
        RestResultBuilder<ProcessResultType, ReturnType> newBuilder = new RestResultBuilder<>(this);
        newBuilder.defaultFailureResult = (RestResult<ReturnType>) failureResult;
        return newBuilder;
    }

    public RestResult<ReturnType> perform(Supplier<ServiceResult<ProcessResultType>> serviceResult) {
        RestResultBuilder<ProcessResultType, ReturnType> newBuilder = new RestResultBuilder<>(this);
        newBuilder.serviceResult = serviceResult;
        return newBuilder.perform();
    }

    @SuppressWarnings("unchecked")
    private RestResult<ReturnType> perform() {

        if (serviceResult != null) {

            try {
                ServiceResult<ProcessResultType> response = serviceResult.get();
                return response.handleFailureOrSuccess(failure -> {

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
                        return (RestResult<ReturnType>) restSuccess(success, OK);
                    }
                });

            } catch (Exception e) {
                LOG.warn("Uncaught exception encountered while performing RestResult processing - returning catch-all error", e);

                if (defaultFailureResult != null) {
                    return defaultFailureResult;
                }

                return (RestResult<ReturnType>) fallbackFailureResult;
            }
        }

        return null;
    }

    private RestResult<ReturnType> handleServiceFailure(ServiceFailure failure) {
        return restFailure(failure.getErrors());
    }
}
