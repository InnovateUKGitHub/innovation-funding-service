package org.innovateuk.ifs.commons.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ErrorHolder;
import org.innovateuk.ifs.util.Either;
import org.innovateuk.ifs.util.ExceptionThrowingConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.util.CollectionFunctions.nullSafe;

/**
 * Represents the result of an action, that will be either a failure or a success.  A failure will result in a FailureType, and a
 * success will result in a T.  Additionally, these can be mapped to produce new ServiceResults that either fail or succeed.
 */
public abstract class BaseFailingOrSucceedingResult<T, FailureType extends ErrorHolder> implements FailingOrSucceedingResult<T, FailureType> {

    private static final Log LOG = LogFactory.getLog(BaseFailingOrSucceedingResult.class);

    protected Either<FailureType, T> result;

    protected BaseFailingOrSucceedingResult(BaseFailingOrSucceedingResult<T, FailureType> original) {
        this.result = original.result;
    }

    protected BaseFailingOrSucceedingResult(Either<FailureType, T> result) {
        this.result = result;
    }

    @Override
    public <T1> T1 handleSuccessOrFailure(ExceptionThrowingFunction<? super FailureType, ? extends T1> failureHandler,
                                          ExceptionThrowingFunction<? super T, ? extends T1> successHandler) {
        return mapLeftOrRight(failureHandler, successHandler);
    }

    @Override
    public BaseFailingOrSucceedingResult<T, FailureType> handleSuccessOrFailureNoReturn(ExceptionThrowingConsumer<? super FailureType> failureHandler,
                                                                                        ExceptionThrowingConsumer<? super T> successHandler) {

        handleSuccessOrFailure(failure -> {
            failureHandler.accept(failure);
            return null;
        }, success -> {
            successHandler.accept(success);
            return null;
        });

        return this;
    }

    @Override
    public T getOrElse(ExceptionThrowingFunction<FailureType, T> failureHandler) {
        return handleSuccessOrFailure(failureHandler, i -> i);
    }

    @Override
    public T getOrElse(T orElse) {
        return getOrElse(failureType -> orElse);
    }

    @Override
    public <R> BaseFailingOrSucceedingResult<R, FailureType> andOnSuccess(ExceptionThrowingFunction<? super T, FailingOrSucceedingResult<R, FailureType>> successHandler) {
        return map(successHandler);
    }

    @Override
    public BaseFailingOrSucceedingResult<Void, FailureType> andOnSuccessReturnVoid(Runnable successHandler) {

        if (isLeft()) {
            return (BaseFailingOrSucceedingResult<Void, FailureType>) this;
        }

        try {
            successHandler.run();
            return (BaseFailingOrSucceedingResult<Void, FailureType>) this;
        } catch (Exception e) {
            LOG.warn("Exception caught while processing success function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public BaseFailingOrSucceedingResult<T, FailureType> andOnSuccess(Runnable successHandler) {

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
    public BaseFailingOrSucceedingResult<T, FailureType> andOnSuccessDo(Consumer<T> successHandler) {

        if (isLeft()) {
            return this;
        }

        try {
            successHandler.accept(getSuccess());
            return this;
        } catch (Exception e) {
            LOG.warn("Exception caught while processing success function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <R> FailingOrSucceedingResult<R, FailureType> andOnSuccess(Supplier<? extends FailingOrSucceedingResult<R, FailureType>> successHandler) {
        if (isLeft()) {
            return (BaseFailingOrSucceedingResult<R, FailureType>) this;
        }

        try {
            return successHandler.get();
        } catch (Exception e) {
            LOG.warn("Exception caught while processing success function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <R> BaseFailingOrSucceedingResult<R, FailureType> andOnSuccessReturn(Supplier<R> successHandler) {

        if (isLeft()) {
            return (BaseFailingOrSucceedingResult<R, FailureType>) this;
        }

        try {
            R successResult = successHandler.get();
            return createSuccess(successResult);
        } catch (Exception e) {
            LOG.warn("Exception caught while processing success function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public <R> BaseFailingOrSucceedingResult<R, FailureType> andOnSuccessReturn(ExceptionThrowingFunction<? super T, R> successHandler) {
        return flatMap(successHandler);
    }

    
    public <R> FailingOrSucceedingResult<R, FailureType> andOnFailure(Function<FailureType, FailingOrSucceedingResult<R, FailureType>> failureHandler) {
        if (isRight()) {
            return (BaseFailingOrSucceedingResult<R, FailureType>) this;
        }

        try {
            return failureHandler.apply(this.getFailure());
        } catch (Exception e) {
            LOG.warn("Exception caught while processing failure function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public <R> FailingOrSucceedingResult<R, FailureType> andOnFailure(Supplier<FailingOrSucceedingResult<R, FailureType>> failureHandler) {
        return andOnFailure(failure -> {
            return failureHandler.get();
        });
    }

    public <R> FailingOrSucceedingResult<R, FailureType> andOnFailure(Runnable failureHandler) {
        return (BaseFailingOrSucceedingResult<R, FailureType>)andOnFailure(failure -> {
            failureHandler.run();
            return this;
        });
    }

    public <R> FailingOrSucceedingResult<R, FailureType> andOnFailure(Consumer<FailureType> failureHandler) {
        return (BaseFailingOrSucceedingResult<R, FailureType>)andOnFailure(failure -> {
            failureHandler.accept(failure);
            return this;
        });
    }

    public void ifSuccessful(Consumer<? super T> successHandler) {
        if (isSuccess()) {
            successHandler.accept(getSuccess());
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
    public T getSuccess() {
        return isRight() ? getRight() : findAndThrowException(getLeft());
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

    public abstract T findAndThrowException(FailureType failureType);

    protected <T1> T1 mapLeftOrRight(ExceptionThrowingFunction<? super FailureType, ? extends T1> lFunc, ExceptionThrowingFunction<? super T, ? extends T1> rFunc) {
        return result.mapLeftOrRight(lFunc, rFunc);
    }

    protected <R> BaseFailingOrSucceedingResult<R, FailureType> map(ExceptionThrowingFunction<? super T, FailingOrSucceedingResult<R, FailureType>> rFunc) {

        if (result.isLeft()) {
            return createFailure((FailingOrSucceedingResult<R, FailureType>) this);
        }

        try {
            FailingOrSucceedingResult<R, FailureType> successResult = rFunc.apply(result.getRight());
            return successResult.isFailure() ? createFailure(successResult) : createSuccess(successResult);
        } catch (Exception e) {
            LOG.warn("Exception caught while processing success function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    protected <R> BaseFailingOrSucceedingResult<R, FailureType> flatMap(ExceptionThrowingFunction<? super T, R> rFunc) {

        if (result.isLeft()) {
            return createFailure((FailingOrSucceedingResult<R, FailureType>) this);
        }

        try {
            R successResult = rFunc.apply(result.getRight());
            return createSuccess(successResult);
        } catch (Exception e) {
            LOG.warn("Exception caught while processing success function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    protected abstract <R> BaseFailingOrSucceedingResult<R, FailureType> createSuccess(FailingOrSucceedingResult<R, FailureType> success);

    protected abstract <R> BaseFailingOrSucceedingResult<R, FailureType> createSuccess(R success);

    protected abstract <R> BaseFailingOrSucceedingResult<R, FailureType> createFailure(FailureType failure);

    protected abstract <R> BaseFailingOrSucceedingResult<R, FailureType> createFailure(FailingOrSucceedingResult<R, FailureType> failure);

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
     * Function to aggregate a {@link List} of {@link BaseFailingOrSucceedingResult}.
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
            Result extends BaseFailingOrSucceedingResult<List<Item>, FailureType>,
            Input extends BaseFailingOrSucceedingResult<Item, FailureType>>
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
                items.add(i.getSuccess());
            } else {
                failures.add(i.getFailure());
            }
        }
        if (failures.isEmpty()) {
            return (Result) firstResult.createSuccess(items);
        } else {
            final BaseFailingOrSucceedingResult<Item, FailureType> failure = firstResult.createFailure(failures.stream().reduce(null, nullSafe(failureCollector)));
            return (Result) failure;
        }
    }


    public static <T, FailureType extends ErrorHolder, R extends BaseFailingOrSucceedingResult<T, FailureType>> List<R> filterErrors(final List<R> results, Predicate<FailureType> errorsFilter){
        return results.stream().filter(result -> result.isSuccess() ? true : errorsFilter.test(result.getFailure())).collect(toList());
    }
}
