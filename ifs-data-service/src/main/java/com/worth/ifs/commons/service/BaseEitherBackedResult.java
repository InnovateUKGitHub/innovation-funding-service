package com.worth.ifs.commons.service;

import com.worth.ifs.util.Either;

import java.util.function.Function;

/**
 * Represents the result of an action, that will be either a failure or a success.  A failure will result in a FailureType, and a
 * success will result in a T.  Additionally, these can be mapped to produce new ServiceResults that either fail or succeed.
 */
public abstract class BaseEitherBackedResult<T, FailureType> implements FailingOrSucceedingResult<T, FailureType> {

    protected Either<FailureType, T> result;

    protected BaseEitherBackedResult(BaseEitherBackedResult<T, FailureType> original) {
        this.result = original.result;
    }

    protected BaseEitherBackedResult(Either<FailureType, T> result) {
        this.result = result;
    }

    public <T1> T1 handleFailureOrSuccess(Function<? super FailureType, ? extends T1> failureHandler, Function<? super T, ? extends T1> successHandler) {
        return mapLeftOrRight(failureHandler, successHandler);
    }

    @Override
    public <R> R handleSuccessOrFailure(Function<? super FailureType, ? extends R> failureHandler, Function<? super T, ? extends R> successHandler) {
        return mapLeftOrRight(failureHandler, successHandler);
    }

    @Override
    public <R> BaseEitherBackedResult<R, FailureType> andOnSuccess(Function<? super T, FailingOrSucceedingResult<R, FailureType>> successHandler) {
        return map(successHandler);
    }

    public boolean isSuccess() {
        return isRight();
    }

    public boolean isFailure() {
        return isLeft();
    }

    public FailureType getFailure() {
        return getLeft();
    }

    public T getSuccessObject() {
        return getRight();
    }

    protected <T1> T1 mapLeftOrRight(Function<? super FailureType, ? extends T1> lFunc, Function<? super T, ? extends T1> rFunc) {
        return result.mapLeftOrRight(lFunc, rFunc);
    }

    protected <R> BaseEitherBackedResult<R, FailureType> map(Function<? super T, FailingOrSucceedingResult<R, FailureType>> rFunc) {

        if (result.isLeft()) {
            return createFailure((FailingOrSucceedingResult<R, FailureType>) this);
        }

        FailingOrSucceedingResult<R, FailureType> successResult = rFunc.apply(result.getRight());
        return successResult.isFailure() ? createFailure(successResult) : createSuccess(successResult);
    }

    protected abstract <R> BaseEitherBackedResult<R, FailureType> createSuccess(FailingOrSucceedingResult<R, FailureType> success);

    protected abstract <R> BaseEitherBackedResult<R, FailureType> createFailure(FailingOrSucceedingResult<R, FailureType> failure);

    private boolean isLeft() {
        return result.isLeft();
    }

    private boolean isRight() {
        return result.isRight();
    }

    private FailureType getLeft() {
        return result.getLeft();
    }

    private T getRight() {
        return result.getRight();
    }
}
