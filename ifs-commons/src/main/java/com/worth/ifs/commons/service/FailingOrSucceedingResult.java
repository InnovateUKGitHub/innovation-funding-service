package com.worth.ifs.commons.service;

import com.worth.ifs.commons.error.ErrorHolder;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * An interface that represents a result of some process that can either fail or succeed.  Additionally, it can then be chained with
 * other functions to produce results in the event of successes or failures by "mapping" over this object with the "andOnSuccess()" and
 * handleSuccessOrFailure() methods
 */
public interface FailingOrSucceedingResult<SuccessType, FailureType> extends ErrorHolder {

    SuccessType getSuccessObject();

    boolean isSuccess();

    boolean isFailure();

    FailureType getFailure();

    <R> FailingOrSucceedingResult<R, FailureType> andOnSuccess(ExceptionThrowingFunction<? super SuccessType, FailingOrSucceedingResult<R, FailureType>> successHandler);

    <R> FailingOrSucceedingResult<R, FailureType> andOnSuccess(Runnable successHandler);

    FailingOrSucceedingResult<Void, FailureType> andOnSuccessReturnVoid(Runnable successHandler);

    <R> FailingOrSucceedingResult<R, FailureType> andOnSuccess(Supplier<FailingOrSucceedingResult<R, FailureType>> successHandler);

    <R> FailingOrSucceedingResult<R, FailureType> andOnSuccessReturn(Supplier<R> successHandler);

    <R> FailingOrSucceedingResult<R, FailureType> andOnSuccessReturn(ExceptionThrowingFunction<? super SuccessType, R> successHandler);

    <R> FailingOrSucceedingResult<R, FailureType> andOnFailure(Supplier<FailingOrSucceedingResult<R, FailureType>> failureHandler);

    <R> R handleSuccessOrFailure(ExceptionThrowingFunction<? super FailureType, ? extends R> failureHandler, ExceptionThrowingFunction<? super SuccessType, ? extends R> successHandler);

    Optional<SuccessType> getOptionalSuccessObject();
}
