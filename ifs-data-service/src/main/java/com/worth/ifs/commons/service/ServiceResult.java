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
public class ServiceResult<T> implements FailingOrSucceedingResult<T, ServiceFailure> {

    private static final Log LOG = LogFactory.getLog(ServiceResult.class);

    private Either<ServiceFailure, T> result;

    protected ServiceResult(ServiceResult<T> original) {
        this.result = original.result;
    }

    private ServiceResult(T success) {
        this(right(success));
    }

    private ServiceResult(ServiceFailure failure) {
        this(left(failure));
    }

    private ServiceResult(Either<ServiceFailure, T> result) {
        this.result = result;
    }

    public <T1> T1 handleFailureOrSuccess(Function<? super ServiceFailure, ? extends T1> failureHandler, Function<? super T, ? extends T1> successHandler) {
        return mapLeftOrRight(failureHandler, successHandler);
    }

    @Override
    public <R> R handleSuccessOrFailure(Function<? super ServiceFailure, ? extends R> failureHandler, Function<? super T, ? extends R> successHandler) {
        return mapLeftOrRight(failureHandler, successHandler);
    }

    public <R> ServiceResult<R> andOnSuccess(Function<? super T, FailingOrSucceedingResult<R, ServiceFailure>> successHandler) {
        return map(successHandler);
    }

    public boolean isSuccess() {
        return isRight();
    }

    public boolean isFailure() {
        return isLeft();
    }

    public ServiceFailure getFailure() {
        return getLeft();
    }

    public T getSuccessObject() {
        return getRight();
    }

    private <T1> T1 mapLeftOrRight(Function<? super ServiceFailure, ? extends T1> lFunc, Function<? super T, ? extends T1> rFunc) {
        return result.mapLeftOrRight(lFunc, rFunc);
    }

    private <R> ServiceResult<R> map(Function<? super T, FailingOrSucceedingResult<R, ServiceFailure>> rFunc) {

        if (result.isLeft()) {
            return serviceFailure(result.getLeft());
        }

        FailingOrSucceedingResult<R, ServiceFailure> successResult = rFunc.apply(result.getRight());

        return successResult.handleSuccessOrFailure(
                failure -> ServiceResult.<R> serviceFailure(failure),
                success -> ServiceResult.<R> serviceSuccess(success)
        );
    }

    private boolean isLeft() {
        return result.isLeft();
    }

    private boolean isRight() {
        return result.isRight();
    }

    private ServiceFailure getLeft() {
        return result.getLeft();
    }

    private T getRight() {
        return result.getRight();
    }

    public static <T> ServiceResult<T> serviceSuccess(T successfulResult) {
        return new ServiceResult<>(successfulResult);
    }

    public static <T> ServiceResult<T> serviceFailure(ServiceFailure failure) {
        return new ServiceResult<>(failure);
    }

    public static <T> ServiceResult<T> serviceFailure(Error error) {
        return new ServiceResult<>(new ServiceFailure(singletonList(error)));
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
