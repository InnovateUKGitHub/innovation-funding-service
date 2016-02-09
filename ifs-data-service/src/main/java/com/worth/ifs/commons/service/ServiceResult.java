package com.worth.ifs.commons.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorTemplate;
import com.worth.ifs.commons.error.Errors;
import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Collections.singletonList;

/**
 * Represents the result of an action, that will be either a failure or a success.  A failure will result in a ServiceFailure, and a
 * success will result in a T.  Additionally, these can be mapped to produce new ServiceResults that either fail or succeed.
 */
public class ServiceResult<T> extends BaseEitherBackedResult<T, ServiceFailure> {

    private static final Log LOG = LogFactory.getLog(ServiceResult.class);

    private ServiceResult(Either<ServiceFailure, T> result) {
        super(result);
    }

    @Override
    public <R> R handleSuccessOrFailure(Function<? super ServiceFailure, ? extends R> failureHandler, Function<? super T, ? extends R> successHandler) {
        return super.handleSuccessOrFailure(failureHandler, successHandler);
    }

    @Override
    public <R> ServiceResult<R> andOnSuccess(Function<? super T, FailingOrSucceedingResult<R, ServiceFailure>> successHandler) {
        return (ServiceResult<R>) super.andOnSuccess(successHandler);
    }

    @Override
    protected <R> BaseEitherBackedResult<R, ServiceFailure> createSuccess(FailingOrSucceedingResult<R, ServiceFailure> success) {
        return serviceSuccess(success.getSuccessObject());
    }

    @Override
    protected <R> BaseEitherBackedResult<R, ServiceFailure> createFailure(FailingOrSucceedingResult<R, ServiceFailure> failure) {
        return serviceFailure(failure.getFailure());
    }

    public static ServiceResult<Void> serviceSuccess() {
        return new ServiceResult<>(right(null));
    }

    public static <T> ServiceResult<T> serviceSuccess(T successfulResult) {
        return new ServiceResult<>(right(successfulResult));
    }

    public static <T> ServiceResult<T> serviceFailure(ServiceFailure failure) {
        return new ServiceResult<>(left(failure));
    }

    public static <T> ServiceResult<T> serviceFailure(Error error) {
        return new ServiceResult<>(left(new ServiceFailure(singletonList(error))));
    }

    public static <T> ServiceResult<T> getNonNullValue(T value, Error error) {

        if (value == null) {
            return serviceFailure(error);
        }

        return serviceSuccess(value);
    }

    public static <T, R> ServiceResult<T> processAnyFailuresOrSucceed(List<ServiceResult<R>> results, ServiceResult<T> failureResponse, ServiceResult<T> successResponse) {
        return results.stream().anyMatch(ServiceResult::isFailure) ? failureResponse : successResponse;
    }

    /**
     * This wrapper wraps the serviceCode function and rolls back transactions upon receiving a ServiceFailure
     * response (an Either with a left of ServiceFailure).
     *
     * It will also catch all exceptions thrown from within serviceCode and convert them into ServiceFailures of
     * type GENERAL_UNEXPECTED_ERROR.
     *
     * @param serviceCode - code that performs some process and returns a successful or failing ServiceResult, the state
     *                      of which then allows this wrapper to perform additional actions
     *
     * @param <T> - the successful return type of the ServiceResult
     * @return the original ServiceResult returned from the serviceCode, or a generic ServiceResult failure if an exception
     * was thrown in serviceCode
     */
    public static <T> ServiceResult<T> handlingErrors(Supplier<ServiceResult<T>> serviceCode) {
        return handlingErrors(Errors.internalServerErrorError(), serviceCode);
    }

    public static <T> ServiceResult<T> handlingErrors(ErrorTemplate catchAllErrorTemplate, Supplier<ServiceResult<T>> serviceCode) {
        return handlingErrors(new Error(catchAllErrorTemplate), serviceCode);
    }

        /**
         * This wrapper wraps the serviceCode function and rolls back transactions upon receiving a ServiceFailure
         * response (an Either with a left of ServiceFailure).
         *
         * It will also catch all exceptions thrown from within serviceCode and convert them into ServiceFailures of
         * type GENERAL_UNEXPECTED_ERROR.
         *
         * @param <T> - the successful return type of the ServiceResult
         * @param serviceCode - code that performs some process and returns a successful or failing ServiceResult, the state
         *                      of which then allows this wrapper to perform additional actions
         * @return the original ServiceResult returned from the serviceCode, or a generic ServiceResult failure if an exception
         * was thrown in serviceCode
         */
    public static <T> ServiceResult<T> handlingErrors(Error catchAllError, Supplier<ServiceResult<T>> serviceCode) {
        try {
            ServiceResult<T> response = serviceCode.get();

            if (response.isFailure()) {
                LOG.debug("Service failure encountered - performing transaction rollback");
                rollbackTransaction();
            }
            return response;
        } catch (Exception e) {
            LOG.warn("Uncaught exception encountered while performing service call.  Performing transaction rollback and returning ServiceFailure", e);
            rollbackTransaction();
            return serviceFailure(catchAllError);
        }
    }

    private static void rollbackTransaction() {
        try {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        } catch (NoTransactionException e) {
            LOG.trace("No transaction to roll back");
            LOG.error(e);
        }
    }


}
