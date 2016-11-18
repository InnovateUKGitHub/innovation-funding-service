package com.worth.ifs.util;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.ExceptionThrowingFunction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_OPTIONAL_ENTRY_EXPECTED;
import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_SINGLE_ENTRY_EXPECTED;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static java.util.Optional.ofNullable;

/**
 * TODO rationalise as much as possible with ServiceLookupCallbacks
 * Utility class to provide common use case wrappers that can be used to wrap callbacks that require either an entity or
 * some failure message if that entity cannot be found.
 */
public class RestLookupCallbacks {

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(RestLookupCallbacks.class);

    public static <SuccessType> RestResult<SuccessType> find(
            SuccessType result,
            Error failureResponse) {

        if (result instanceof Collection && ((Collection) result).isEmpty()) {
            return RestResult.restFailure(failureResponse);
        }
        if (result instanceof Optional) {
            return ((Optional<SuccessType>) result).map(RestResult::restSuccess).orElse(RestResult.restFailure(failureResponse));
        }

        return ofNullable(result).map(RestResult::restSuccess).orElse(RestResult.restFailure(failureResponse));
    }

    public static <T> RestResult<T> find(
            Optional<T> result,
            Error failureResponse) {

        if(result.isPresent()){
            return RestResult.restSuccess(result.get());
        }else{
            return RestResult.restFailure(failureResponse);
        }
    }

    /**
     * This find() method, given 2 RestResult suppliers, supplies a RestResultTuple2Handler that is able to execute
     * the RestResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful RestResult values as its 2 inputs
     */
    public static <SuccessType1> RestResultHandler<SuccessType1> find(
            Supplier<RestResult<SuccessType1>> getterFn) {

        return new RestResultHandler<>(getterFn);
    }

    /**
     * This find() method, given 2 RestResult suppliers, supplies a RestResultTuple2Handler that is able to execute
     * the RestResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful RestResult values as its 2 inputs
     */
    public static <SuccessType1> RestResultHandler<SuccessType1> find(
            RestResult<SuccessType1> getterFn) {

        return find(() -> getterFn);
    }

    /**
     * This find() method, given 2 RestResult suppliers, supplies a RestResultTuple2Handler that is able to execute
     * the RestResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful RestResult values as its 2 inputs
     */
    public static <SuccessType1, SuccessType2> RestResultTuple2Handler<SuccessType1, SuccessType2> find(
            Supplier<RestResult<SuccessType1>> getterFn1,
            Supplier<RestResult<SuccessType2>> getterFn2) {

        return new RestResultTuple2Handler<>(getterFn1, getterFn2);
    }

    /**
     * This find() method, given 2 RestResult suppliers, supplies a RestResultTuple2Handler that is able to execute
     * the RestResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful RestResult values as its 2 inputs
     */
    public static <SuccessType1, SuccessType2> RestResultTuple2Handler<SuccessType1, SuccessType2> find(
            RestResult<SuccessType1> getterFn1,
            RestResult<SuccessType2> getterFn2) {

        return find(() -> getterFn1, () -> getterFn2);
    }

    /**
     * This find() method, given 2 RestResult suppliers, supplies a RestResultTuple2Handler that is able to execute
     * the RestResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful RestResult values as its 2 inputs
     */
    public static <SuccessType1, SuccessType2, SuccessType3> RestResultTuple3Handler<SuccessType1, SuccessType2, SuccessType3> find(
            Supplier<RestResult<SuccessType1>> getterFn1,
            Supplier<RestResult<SuccessType2>> getterFn2,
            Supplier<RestResult<SuccessType3>> getterFn3) {

        return new RestResultTuple3Handler<>(getterFn1, getterFn2, getterFn3);
    }

    /**
     * This find() method, given 2 RestResult suppliers, supplies a RestResultTuple2Handler that is able to execute
     * the RestResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful RestResult values as its 2 inputs
     */
    public static <SuccessType1, SuccessType2, SuccessType3> RestResultTuple3Handler<SuccessType1, SuccessType2, SuccessType3> find(
            RestResult<SuccessType1> getterFn1,
            RestResult<SuccessType2> getterFn2,
            RestResult<SuccessType3> getterFn3) {

        return find(() -> getterFn1, () -> getterFn2, () -> getterFn3);
    }

    /**
     * This find() method, given 2 RestResult suppliers, supplies a RestResultTuple2Handler that is able to execute
     * the RestResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful RestResult values as its 2 inputs
     */
    public static <SuccessType1, SuccessType2, SuccessType3, SuccessType4> RestResultTuple4Handler<SuccessType1, SuccessType2, SuccessType3, SuccessType4> find(
            Supplier<RestResult<SuccessType1>> getterFn1,
            Supplier<RestResult<SuccessType2>> getterFn2,
            Supplier<RestResult<SuccessType3>> getterFn3,
            Supplier<RestResult<SuccessType4>> getterFn4) {

        return new RestResultTuple4Handler<>(getterFn1, getterFn2, getterFn3, getterFn4);
    }

    /**
     * This find() method, given 2 RestResult suppliers, supplies a RestResultTuple2Handler that is able to execute
     * the RestResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful RestResult values as its 2 inputs
     */
    public static <SuccessType1, SuccessType2, SuccessType3, SuccessType4> RestResultTuple4Handler<SuccessType1, SuccessType2, SuccessType3, SuccessType4> find(
            RestResult<SuccessType1> getterFn1,
            RestResult<SuccessType2> getterFn2,
            RestResult<SuccessType3> getterFn3,
            RestResult<SuccessType4> getterFn4) {

        return find(() -> getterFn1, () -> getterFn2, () -> getterFn3, () -> getterFn4);
    }

    public static <T> RestResult<T> getOnlyElementOrFail(Collection<T> list) {
        if (list == null || list.size() != 1) {
            return RestResult.restFailure(new Error(GENERAL_SINGLE_ENTRY_EXPECTED, list != null ? list.size() : null));
        }
        return RestResult.restSuccess(list.iterator().next());
    }
    public static <T> RestResult<T> getOptionalElementOrFail(Optional<T> item) {
        if (!item.isPresent()) {
            return RestResult.restFailure(GENERAL_OPTIONAL_ENTRY_EXPECTED);
        }
        return RestResult.restSuccess(item.get());
    }

    /**
     * This class is produced by the find() method, which given 2 RestResult suppliers, is able to execute the RestResults
     * in a chain and fail early if necessary.  Assuming that they are all successes, a supplied BiFunction can then be called
     * with the 2 successful RestResult values as its 2 inputs
     *
     * @param <T>
     */
    public static class RestResultHandler<T> {

        private Supplier<RestResult<T>> getterFn1;

        public RestResultHandler(Supplier<RestResult<T>> getterFn1) {
            this.getterFn1 = getterFn1;
        }

        public <R> RestResult<R> andOnSuccess(Function<T, RestResult<R>> mainFunction) {
            return getterFn1.get().andOnSuccess(result1 -> mainFunction.apply(result1));
        }

        public <T> RestResult<T> andOnSuccess(Supplier<T> supplier) {
            return getterFn1.get().andOnSuccess(result1 -> RestResult.restSuccess(supplier.get()));
        }

        public <R> RestResult<R> andOnSuccessReturn(ExceptionThrowingFunction<T, R> successFn) {
            return getterFn1.get().andOnSuccessReturn(result1 -> successFn.apply(result1));
        }
    }

    /**
     * This class is produced by the find() method, which given 2 RestResult suppliers, is able to execute the RestResults
     * in a chain and fail early if necessary.  Assuming that they are all successes, a supplied BiFunction can then be called
     * with the 2 successful RestResult values as its 2 inputs
     *
     * @param <R>
     * @param <S>
     */
    public static class RestResultTuple2Handler<R, S> {

        private Supplier<RestResult<R>> getterFn1;
        private Supplier<RestResult<S>> getterFn2;

        public RestResultTuple2Handler(Supplier<RestResult<R>> getterFn1, Supplier<RestResult<S>> getterFn2) {
            this.getterFn1 = getterFn1;
            this.getterFn2 = getterFn2;
        }

        public <T> RestResult<T> andOnSuccess(BiFunction<R, S, RestResult<T>> mainFunction) {
            return getterFn1.get().andOnSuccess(result1 -> getterFn2.get().andOnSuccess(result2 -> mainFunction.apply(result1, result2)));
        }

        public <T> RestResult<T> andOnSuccess(Supplier<T> supplier) {
            return andOnSuccess((p1, p2) -> RestResult.restSuccess(supplier.get()));
        }
    }

    /**
     * This class is produced by the find() method, which given 2 RestResult suppliers, is able to execute the RestResults
     * in a chain and fail early if necessary.  Assuming that they are all successes, a supplied BiFunction can then be called
     * with the 2 successful RestResult values as its 2 inputs
     *
     * @param <R>
     * @param <S>
     */
    public static class RestResultTuple3Handler<R, S, T> {

        private Supplier<RestResult<R>> getterFn1;
        private Supplier<RestResult<S>> getterFn2;
        private Supplier<RestResult<T>> getterFn3;

        public RestResultTuple3Handler(Supplier<RestResult<R>> getterFn1, Supplier<RestResult<S>> getterFn2, Supplier<RestResult<T>> getterFn3) {
            this.getterFn1 = getterFn1;
            this.getterFn2 = getterFn2;
            this.getterFn3 = getterFn3;
        }

        public <A> RestResult<A> andOnSuccess(TriFunction<R, S, T, RestResult<A>> mainFunction) {
            return getterFn1.get().
                    andOnSuccess(result1 -> getterFn2.get().
                            andOnSuccess(result2 -> getterFn3.get().
                                    andOnSuccess(result3 -> mainFunction.apply(result1, result2, result3))));
        }

        public <T> RestResult<T> andOnSuccess(Supplier<T> supplier) {
            return andOnSuccess((p1, p2, p3) -> RestResult.restSuccess(supplier.get()));
        }
    }

    /**
     * This class is produced by the find() method, which given 2 RestResult suppliers, is able to execute the RestResults
     * in a chain and fail early if necessary.  Assuming that they are all successes, a supplied BiFunction can then be called
     * with the 2 successful RestResult values as its 2 inputs
     *
     * @param <R>
     * @param <S>
     */
    public static class RestResultTuple4Handler<R, S, T, U> {

        private Supplier<RestResult<R>> getterFn1;
        private Supplier<RestResult<S>> getterFn2;
        private Supplier<RestResult<T>> getterFn3;
        private Supplier<RestResult<U>> getterFn4;

        public RestResultTuple4Handler(Supplier<RestResult<R>> getterFn1, Supplier<RestResult<S>> getterFn2, Supplier<RestResult<T>> getterFn3, Supplier<RestResult<U>> getterFn4) {
            this.getterFn1 = getterFn1;
            this.getterFn2 = getterFn2;
            this.getterFn3 = getterFn3;
            this.getterFn4 = getterFn4;
        }

        public <A> RestResult<A> andOnSuccess(QuadFunction<R, S, T, U, RestResult<A>> mainFunction) {
            return getterFn1.get().
                    andOnSuccess(result1 -> getterFn2.get().
                            andOnSuccess(result2 -> getterFn3.get().
                                    andOnSuccess(result3 -> getterFn4.get().
                                            andOnSuccess(result4 -> mainFunction.apply(result1, result2, result3, result4)))));
        }

        public <T> RestResult<T> andOnSuccess(Supplier<T> supplier) {
            return andOnSuccess((p1, p2, p3, p4) -> RestResult.restSuccess(supplier.get()));
        }

    }

    @FunctionalInterface
    public interface TriFunction<R, S, T, A> {

        A apply(R r, S s, T t);
    }

    @FunctionalInterface
    public interface QuadFunction<R, S, T, U, A> {

        A apply(R r, S s, T t, U u);
    }
}