package com.worth.ifs.commons.service;

import java.util.function.Function;

/**
 * An interface that represents a result of some process that can either fail or succeed.  Additionally, it can then be chained with
 * other functions to produce results in the event of successes or failures by "mapping" over this object with the "andOnSuccess()" and
 * handleSuccessOrFailure() methods
 */
public interface FailingOrSucceedingResult<SuccessType, FailureType> {

    SuccessType getSuccessObject();

    boolean isSuccess();

    boolean isFailure();

    FailureType getFailure();

    <R> FailingOrSucceedingResult<R, FailureType> andOnSuccess(Function<? super SuccessType, FailingOrSucceedingResult<R, FailureType>> successHandler);

    <R> R handleSuccessOrFailure(Function<? super FailureType, ? extends R> failureHandler, Function<? super SuccessType, ? extends R> successHandler);
}
