package org.innovateuk.ifs.commons.service;

import org.innovateuk.ifs.commons.error.ErrorHolder;
import org.innovateuk.ifs.util.ExceptionThrowingConsumer;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An interface that represents a result of some process that can either fail or succeed.  Additionally, it can then be chained with
 * other functions to produce results in the event of successes or failures by "mapping" over this object with the "andOnSuccess()" and
 * handleSuccessOrFailure() methods
 */
public interface FailingOrSucceedingResult<SuccessType, FailureType> extends ErrorHolder {

    SuccessType getSuccess();

    boolean isSuccess();

    boolean isFailure();

    FailureType getFailure();

    <R> FailingOrSucceedingResult<R, FailureType> andOnSuccess(ExceptionThrowingFunction<? super SuccessType, FailingOrSucceedingResult<R, FailureType>> successHandler);

    <R> FailingOrSucceedingResult<R, FailureType> andOnSuccess(Runnable successHandler);

    FailingOrSucceedingResult<SuccessType, FailureType> andOnSuccessDo(Consumer<SuccessType> successHandler);

    FailingOrSucceedingResult<Void, FailureType> andOnSuccessReturnVoid(Runnable successHandler);

    <R> FailingOrSucceedingResult<R, FailureType> andOnSuccess(Supplier<? extends FailingOrSucceedingResult<R, FailureType>> successHandler);

    <R> FailingOrSucceedingResult<R, FailureType> andOnSuccessReturn(Supplier<R> successHandler);

    <R> FailingOrSucceedingResult<R, FailureType> andOnSuccessReturn(ExceptionThrowingFunction<? super SuccessType, R> successHandler);

    <R> FailingOrSucceedingResult<R, FailureType> andOnFailure(Supplier<FailingOrSucceedingResult<R, FailureType>> failureHandler);

    <R> R handleSuccessOrFailure(ExceptionThrowingFunction<? super FailureType, ? extends R> failureHandler, ExceptionThrowingFunction<? super SuccessType, ? extends R> successHandler);

    FailingOrSucceedingResult<SuccessType, FailureType> handleSuccessOrFailureNoReturn(ExceptionThrowingConsumer<? super FailureType> failureHandler, ExceptionThrowingConsumer<? super SuccessType> successHandler);

    Optional<SuccessType> getOptionalSuccessObject();

    SuccessType getOrElse(ExceptionThrowingFunction<FailureType, SuccessType> failureHandler);

    SuccessType getOrElse(SuccessType orElse);
}
