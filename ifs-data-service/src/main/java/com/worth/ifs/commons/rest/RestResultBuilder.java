package com.worth.ifs.commons.rest;

import com.google.common.base.Supplier;
import com.worth.ifs.commons.service.ServiceFailure;
import com.worth.ifs.commons.service.ServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class RestResultBuilder {

    private static final Log LOG = LogFactory.getLog(RestResultBuilder.class);

    private static RestResult<?> fallbackFailureResult = internalServerErrorRestFailure();
    private Function<? super Object, RestResult<?>> successResult;
    private RestResult<?> defaultFailureResult;

    private RestResultBuilder() {
    }

    private RestResultBuilder(RestResultBuilder existingBuilder) {
        this.successResult = existingBuilder.successResult;
        this.defaultFailureResult = existingBuilder.defaultFailureResult;
    }

    public static RestResultBuilder newRestHandler() {
        return new RestResultBuilder();
    }

    public RestResultBuilder andOnSuccess(RestResult<?> successResult) {
        return andOnSuccess(success -> successResult);
    }

    public <T> RestResultBuilder andOnSuccess(Function<T, RestResult<?>> successResult) {
        RestResultBuilder newBuilder = new RestResultBuilder(this);
        newBuilder.successResult = result -> successResult.apply((T) result);
        return newBuilder;
    }

    @SuppressWarnings("unchecked")
    public RestResultBuilder andWithDefaultFailure(RestResult<?> failureResult) {
        RestResultBuilder newBuilder = new RestResultBuilder(this);
        newBuilder.defaultFailureResult = failureResult;
        return newBuilder;
    }

    public <ReturnType> RestResult<ReturnType> perform(Supplier<ServiceResult<?>> serviceResult) {

        if (serviceResult != null) {

            try {
                ServiceResult<?> response = serviceResult.get();
                return response.handleSuccessOrFailure(failure -> {

                    RestResult<ReturnType> handled = handleServiceFailure(failure);

                    if (handled != null) {
                        return handled;
                    }

                    if (defaultFailureResult != null) {
                        return (RestResult<ReturnType>) defaultFailureResult;
                    } else {
                        return (RestResult<ReturnType>) fallbackFailureResult;
                    }
                }, success -> {
                    if (successResult != null) {
                        return (RestResult<ReturnType>) successResult.apply(success);
                    } else {
                        return (RestResult<ReturnType>) restSuccess(success, OK);
                    }
                });

            } catch (Exception e) {
                LOG.warn("Uncaught exception encountered while performing RestResult processing - returning catch-all error", e);

                if (defaultFailureResult != null) {
                    return (RestResult<ReturnType>) defaultFailureResult;
                }

                return (RestResult<ReturnType>) fallbackFailureResult;
            }
        }

        return null;
    }

    private <ReturnType> RestResult<ReturnType> handleServiceFailure(ServiceFailure failure) {
        return restFailure(failure.getErrors());
    }
}
