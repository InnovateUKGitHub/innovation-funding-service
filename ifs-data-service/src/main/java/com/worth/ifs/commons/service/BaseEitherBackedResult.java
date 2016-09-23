package com.worth.ifs.commons.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorHolder;
import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.worth.ifs.util.CollectionFunctions.nullSafe;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Represents the result of an action, that will be either a failure or a success.  A failure will result in a FailureType, and a
 * success will result in a T.  Additionally, these can be mapped to produce new ServiceResults that either fail or succeed.
 */
public abstract class BaseEitherBackedResult<T, FailureType extends ErrorHolder> implements FailingOrSucceedingResult<T, FailureType> {

    private static final Log LOG = LogFactory.getLog(BaseEitherBackedResult.class);

    protected Either<FailureType, T> result;

    protected BaseEitherBackedResult(BaseEitherBackedResult<T, FailureType> original) {
        this.result = original.result;
    }

    protected BaseEitherBackedResult(Either<FailureType, T> result) {
        this.result = result;
    }

    @Override
    public <T1> T1 handleSuccessOrFailure(ExceptionThrowingFunction<? super FailureType, ? extends T1> failureHandler, ExceptionThrowingFunction<? super T, ? extends T1> successHandler) {
        return mapLeftOrRight(failureHandler, successHandler);
    }

    @Override
    public <R> BaseEitherBackedResult<R, FailureType> andOnSuccess(ExceptionThrowingFunction<? super T, FailingOrSucceedingResult<R, FailureType>> successHandler) {
        return map(successHandler);
    }

    @Override
    public BaseEitherBackedResult<Void, FailureType> andOnSuccessReturnVoid(Runnable successHandler) {

        if (isLeft()) {
            return (BaseEitherBackedResult<Void, FailureType>) this;
        }

        try {
            successHandler.run();
            return (BaseEitherBackedResult<Void, FailureType>) this;
        } catch (Exception e) {
            LOG.warn("Exception caught while processing success function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public BaseEitherBackedResult<T, FailureType> andOnSuccess(Runnable successHandler) {

        if (isLeft()) {
            return this;
        }

        try {
            successHandler.run();
            return this;
        } catch (Exception e) {
            LOG.warn("Exception caught while processing success function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <R> FailingOrSucceedingResult<R, FailureType> andOnSuccess(Supplier<FailingOrSucceedingResult<R, FailureType>> successHandler) {
        if (isLeft()) {
            return (BaseEitherBackedResult<R, FailureType>) this;
        }

        try {
            return successHandler.get();
        } catch (Exception e) {
            LOG.warn("Exception caught while processing success function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <R> BaseEitherBackedResult<R, FailureType> andOnSuccessReturn(Supplier<R> successHandler) {

        if (isLeft()) {
            return (BaseEitherBackedResult<R, FailureType>) this;
        }

        try {
            R result = successHandler.get();
            return createSuccess(result);
        } catch (Exception e) {
            LOG.warn("Exception caught while processing success function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public <R> BaseEitherBackedResult<R, FailureType> andOnSuccessReturn(ExceptionThrowingFunction<? super T, R> successHandler) {
        return flatMap(successHandler);
    }

    @Override
    public <R> FailingOrSucceedingResult<R, FailureType> andOnFailure(Supplier<FailingOrSucceedingResult<R, FailureType>>  failureHandler) {
        if (isRight()) {
            return (BaseEitherBackedResult<R, FailureType>) this;
        }

        try {
            return failureHandler.get();
        } catch (Exception e) {
            LOG.warn("Exception caught while processing failure function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSuccess() {
        return isRight();
    }

    @Override
    public boolean isFailure() {
        return isLeft();
    }

    @Override
    public FailureType getFailure() {
        return getLeft();
    }

    @Override
    public T getSuccessObject() {
        return getRight();
    }

    @Override
    public Optional<T> getOptionalSuccessObject() {
        return isRight() ? Optional.ofNullable(getRight()) : Optional.empty();
    }

    @Override
    public List<Error> getErrors() {
        return isRight() ? emptyList() : getFailure().getErrors();
    }

    // TODO DW - INFUND-1555 - remove "BACKWARDS COMPATIBILITY" method here (for "not found" nulls)

    /**
     * @deprecated Should handled either success or failure case explicitly, usually by using handlesuccessorfailure()
     */
    @Deprecated
    public T getSuccessObjectOrNull() {
        return isRight() ? getRight() : null;
    }

    public T getSuccessObjectOrThrowException() {
        return isRight() ? getRight() : findAndThrowException(getLeft());
    }

    public abstract T findAndThrowException(FailureType failureType);

    protected <T1> T1 mapLeftOrRight(ExceptionThrowingFunction<? super FailureType, ? extends T1> lFunc, ExceptionThrowingFunction<? super T, ? extends T1> rFunc) {
        return result.mapLeftOrRight(lFunc, rFunc);
    }

    protected <R> BaseEitherBackedResult<R, FailureType> map(ExceptionThrowingFunction<? super T, FailingOrSucceedingResult<R, FailureType>> rFunc) {

        if (result.isLeft()) {
            return createFailure((FailingOrSucceedingResult<R, FailureType>) this);
        }

        try {
            FailingOrSucceedingResult<R, FailureType> successResult = rFunc.apply(result.getRight());
            return successResult.isFailure() ? createFailure(successResult) : createSuccess(successResult);
        } catch (Throwable e) {
            LOG.warn("Exception caught while processing success function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    protected <R> BaseEitherBackedResult<R, FailureType> flatMap(ExceptionThrowingFunction<? super T, R> rFunc) {

        if (result.isLeft()) {
            return createFailure((FailingOrSucceedingResult<R, FailureType>) this);
        }

        try {
            R successResult = rFunc.apply(result.getRight());
            return createSuccess(successResult);
        } catch (Throwable e) {
            LOG.warn("Exception caught while processing success function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    protected abstract <R> BaseEitherBackedResult<R, FailureType> createSuccess(FailingOrSucceedingResult<R, FailureType> success);

    protected abstract <R> BaseEitherBackedResult<R, FailureType> createSuccess(R success);

    protected abstract <R> BaseEitherBackedResult<R, FailureType> createFailure(FailureType failure);

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


    /**
     * Function to aggregate a {@link List} of {@link BaseEitherBackedResult}.
     *
     * @param input
     * @param failureCollector
     * @param emptyResult
     * @param <Item>
     * @param <FailureType>
     * @param <Result>
     * @param <Input>
     * @return
     */
    protected static <Item,
            FailureType extends ErrorHolder,
            Result extends BaseEitherBackedResult<List<Item>, FailureType>,
            Input extends BaseEitherBackedResult<Item, FailureType>>
    Result aggregate(final List<Input> input,
                     final BinaryOperator<FailureType> failureCollector,
                     final Result emptyResult) {
        if (input == null || input.isEmpty()) {
            return emptyResult;
        }
        final Input firstResult = input.get(0);
        final List<Item> items = new ArrayList<>();
        final List<FailureType> failures = new ArrayList<FailureType>();
        for (final Input i : input) {
            if (i.isSuccess()) {
                items.add(i.getSuccessObject());
            } else {
                failures.add(i.getFailure());
            }
        }
        if (failures.isEmpty()) {
            return (Result) firstResult.createSuccess(items);
        } else {
            final BaseEitherBackedResult<Item, FailureType> failure = firstResult.createFailure(failures.stream().reduce(null, nullSafe(failureCollector)));
            return (Result) failure;
        }
    }


    public static <T, FailureType extends ErrorHolder, R extends BaseEitherBackedResult<T, FailureType>> List<R> filterErrors(final List<R> results, Predicate<FailureType> errorsFilter){
        return results.stream().filter(result -> result.isSuccess() ? true : errorsFilter.test(result.getFailure())).collect(toList());
    }
}
