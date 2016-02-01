package com.worth.ifs.transactional;

import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.transactional.BaseTransactionalService.Failures.UNEXPECTED_ERROR;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Collections.singletonList;

/**
 * Represents the result of an action, that will be either a failure or a success.  A failure will result in a ServiceFailure, and a
 * success will result in a T.  Additionally, these can be mapped to produce new ServiceResults that either fail or succeed.
 */
public class ServiceResult<T> {

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

    public <T1> T1 mapLeftOrRight(Function<? super ServiceFailure, ? extends T1> lFunc, Function<? super T, ? extends T1> rFunc) {
        return result.mapLeftOrRight(lFunc, rFunc);
    }

    public <R> ServiceResult<R> map(Function<? super T, ServiceResult<R>> rFunc) {

        if (result.isLeft()) {
            return serviceFailure(result.getLeft());
        }

        return rFunc.apply(result.getRight());
    }

    public boolean isLeft() {
        return result.isLeft();
    }

    public boolean isRight() {
        return result.isRight();
    }

    public ServiceFailure getLeft() {
        return result.getLeft();
    }

    public T getRight() {
        return result.getRight();
    }

    public static <T1> T1 getLeftOrRight(Either<T1, T1> either) {
        return Either.getLeftOrRight(either);
    }

    public static <T> ServiceResult<T> serviceSuccess(T successfulResult) {
        return new ServiceResult<>(successfulResult);
    }

    public static <T> Supplier<ServiceResult<T>> serviceSuccessSupplier(T successfulResult) {
        return () -> serviceSuccess(successfulResult);
    }

    public static <T> ServiceResult<T> serviceFailure(ServiceFailure failure) {
        return new ServiceResult<>(failure);
    }

    public static <T> ServiceResult<T> serviceFailure(Error error) {
        return new ServiceResult<>(new ServiceFailure(singletonList(error)));
    }

    public static <T> ServiceResult<T> nonNull(T value, Error error) {

        if (value == null) {
            return serviceFailure(error);
        }

        return serviceSuccess(value);
    }

    public static <T> ServiceResult<T> fromEither(Either<ServiceFailure, T> value) {
        return new ServiceResult<>(value);
    }

    public static <T, R> ServiceResult<T> anyFailures(List<ServiceResult<R>> results, ServiceResult<T> failureResponse, ServiceResult<T> successResponse) {
        return results.stream().anyMatch(ServiceResult::isLeft) ? failureResponse : successResponse;
    }

    /**
     * This wrapper wraps the serviceCode function and rolls back transactions upon receiving a ServiceFailure
     * response (an Either with a left of ServiceFailure).
     *
     * It will also catch all exceptions thrown from within serviceCode and convert them into ServiceFailures of
     * type UNEXPECTED_ERROR.
     *
     * @param serviceCode
     * @param <T>
     * @return
     */
    public static <T> ServiceResult<T> handlingErrors(Supplier<ServiceResult<T>> serviceCode) {
        return handlingErrors(new Error(UNEXPECTED_ERROR), serviceCode);
    }

    /**
     * This wrapper wraps the serviceCode function and rolls back transactions upon receiving a ServiceFailure
     * response (an Either with a left of ServiceFailure).
     *
     * It will also catch all exceptions thrown from within serviceCode and convert them into ServiceFailures of
     * type UNEXPECTED_ERROR.
     *
     * @param <T>
     * @param serviceCode
     * @return
     */
    public static <T> ServiceResult<T> handlingErrors(Error catchAllError, Supplier<ServiceResult<T>> serviceCode) {
        try {
            ServiceResult<T> response = serviceCode.get();

            if (response.isLeft()) {
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
