package com.worth.ifs.util;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ExceptionThrowingFunction;
import com.worth.ifs.commons.service.ServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Optional;
import java.util.function.*;

import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_OPTIONAL_ENTRY_EXPECTED;
import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_SINGLE_ENTRY_EXPECTED;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static java.util.Optional.ofNullable;

/**
 * Utility class to provide common use case wrappers that can be used to wrap callbacks that require either an entity or
 * some failure message if that entity cannot be found.
 */
public class EntityLookupCallbacks {

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(EntityLookupCallbacks.class);

    public static <SuccessType> ServiceResult<SuccessType> find(
            SuccessType result,
            Error failureResponse) {

        if (result instanceof Collection && ((Collection) result).isEmpty()) {
            return ServiceResult.serviceFailure(failureResponse);
        }
        if (result instanceof Optional) {
            return ((Optional<SuccessType>) result).map(ServiceResult::serviceSuccess).orElse(ServiceResult.serviceFailure(failureResponse));
        }

        return ofNullable(result).map(ServiceResult::serviceSuccess).orElse(ServiceResult.serviceFailure(failureResponse));
    }

    public static <T> ServiceResult<T> find(
            Optional<T> result,
            Error failureResponse) {

        if(result.isPresent()){
            return ServiceResult.serviceSuccess(result.get());
        }else{
            return ServiceResult.serviceFailure(failureResponse);
        }
    }

    /**
     * This find() method, given 2 ServiceResult suppliers, supplies a ServiceResultTuple2Handler that is able to execute
     * the ServiceResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful ServiceResult values as its 2 inputs
     */
    public static <SuccessType1> ServiceResultHandler<SuccessType1> find(
            Supplier<ServiceResult<SuccessType1>> getterFn) {

        return new ServiceResultHandler<>(getterFn);
    }

    /**
     * This find() method, given 2 ServiceResult suppliers, supplies a ServiceResultTuple2Handler that is able to execute
     * the ServiceResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful ServiceResult values as its 2 inputs
     */
    public static <SuccessType1> ServiceResultHandler<SuccessType1> find(
            ServiceResult<SuccessType1> getterFn) {

        return find(() -> getterFn);
    }

    /**
     * This find() method, given 2 ServiceResult suppliers, supplies a ServiceResultTuple2Handler that is able to execute
     * the ServiceResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful ServiceResult values as its 2 inputs
     */
    public static <SuccessType1, SuccessType2> ServiceResultTuple2Handler<SuccessType1, SuccessType2> find(
            Supplier<ServiceResult<SuccessType1>> getterFn1,
            Supplier<ServiceResult<SuccessType2>> getterFn2) {

        return new ServiceResultTuple2Handler<>(getterFn1, getterFn2);
    }

    /**
     * This find() method, given 2 ServiceResult suppliers, supplies a ServiceResultTuple2Handler that is able to execute
     * the ServiceResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful ServiceResult values as its 2 inputs
     */
    public static <SuccessType1, SuccessType2> ServiceResultTuple2Handler<SuccessType1, SuccessType2> find(
            ServiceResult<SuccessType1> getterFn1,
            ServiceResult<SuccessType2> getterFn2) {

        return find(() -> getterFn1, () -> getterFn2);
    }

    /**
     * This find() method, given 2 ServiceResult suppliers, supplies a ServiceResultTuple2Handler that is able to execute
     * the ServiceResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful ServiceResult values as its 2 inputs
     */
    public static <SuccessType1, SuccessType2, SuccessType3> ServiceResultTuple3Handler<SuccessType1, SuccessType2, SuccessType3> find(
            Supplier<ServiceResult<SuccessType1>> getterFn1,
            Supplier<ServiceResult<SuccessType2>> getterFn2,
            Supplier<ServiceResult<SuccessType3>> getterFn3) {

        return new ServiceResultTuple3Handler<>(getterFn1, getterFn2, getterFn3);
    }

    /**
     * This find() method, given 2 ServiceResult suppliers, supplies a ServiceResultTuple2Handler that is able to execute
     * the ServiceResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful ServiceResult values as its 2 inputs
     */
    public static <SuccessType1, SuccessType2, SuccessType3> ServiceResultTuple3Handler<SuccessType1, SuccessType2, SuccessType3> find(
            ServiceResult<SuccessType1> getterFn1,
            ServiceResult<SuccessType2> getterFn2,
            ServiceResult<SuccessType3> getterFn3) {

        return find(() -> getterFn1, () -> getterFn2, () -> getterFn3);
    }

    /**
     * This find() method, given 2 ServiceResult suppliers, supplies a ServiceResultTuple2Handler that is able to execute
     * the ServiceResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful ServiceResult values as its 2 inputs
     */
    public static <SuccessType1, SuccessType2, SuccessType3, SuccessType4> ServiceResultTuple4Handler<SuccessType1, SuccessType2, SuccessType3, SuccessType4> find(
            Supplier<ServiceResult<SuccessType1>> getterFn1,
            Supplier<ServiceResult<SuccessType2>> getterFn2,
            Supplier<ServiceResult<SuccessType3>> getterFn3,
            Supplier<ServiceResult<SuccessType4>> getterFn4) {

        return new ServiceResultTuple4Handler<>(getterFn1, getterFn2, getterFn3, getterFn4);
    }

    /**
     * This find() method, given 2 ServiceResult suppliers, supplies a ServiceResultTuple2Handler that is able to execute
     * the ServiceResults in a chain and fail early if necessary.  Assuming that they are all successes, a supplied
     * BiFunction can then be called with the 2 successful ServiceResult values as its 2 inputs
     */
    public static <SuccessType1, SuccessType2, SuccessType3, SuccessType4> ServiceResultTuple4Handler<SuccessType1, SuccessType2, SuccessType3, SuccessType4> find(
            ServiceResult<SuccessType1> getterFn1,
            ServiceResult<SuccessType2> getterFn2,
            ServiceResult<SuccessType3> getterFn3,
            ServiceResult<SuccessType4> getterFn4) {

        return find(() -> getterFn1, () -> getterFn2, () -> getterFn3, () -> getterFn4);
    }

    public static <T> ServiceResult<T> getOnlyElementOrFail(Collection<T> list) {
        if (list == null || list.size() != 1) {
            return ServiceResult.serviceFailure(new Error(GENERAL_SINGLE_ENTRY_EXPECTED, list != null ? list.size() : null));
        }
        return ServiceResult.serviceSuccess(list.iterator().next());
    }

    public static <T> ServiceResult<T> getOptionalElementOrFail(Optional<T> item) {
        if (!item.isPresent()) {
            return ServiceResult.serviceFailure(GENERAL_OPTIONAL_ENTRY_EXPECTED);
        }
        return ServiceResult.serviceSuccess(item.get());
    }

    /**
     * This class is produced by the find() method, which given 2 ServiceResult suppliers, is able to execute the ServiceResults
     * in a chain and fail early if necessary.  Assuming that they are all successes, a supplied BiFunction can then be called
     * with the 2 successful ServiceResult values as its 2 inputs
     *
     * @param <T>
     */
    public static class ServiceResultHandler<T> {

        private Supplier<ServiceResult<T>> getterFn1;

        public ServiceResultHandler(Supplier<ServiceResult<T>> getterFn1) {
            this.getterFn1 = getterFn1;
        }

        public <R> ServiceResult<R> andOnSuccess(Function<T, ServiceResult<R>> mainFunction) {
            return getterFn1.get().andOnSuccess(result1 -> mainFunction.apply(result1));
        }

        public <T> ServiceResult<T> andOnSuccess(Supplier<T> supplier) {
            return getterFn1.get().andOnSuccess(result1 -> ServiceResult.serviceSuccess(supplier.get()));
        }

        public <R> ServiceResult<R> andOnSuccessReturn(ExceptionThrowingFunction<T, R> successFn) {
            return getterFn1.get().andOnSuccessReturn(result1 -> successFn.apply(result1));
        }

        public ServiceResult<Void> andOnSuccessReturnVoid(Consumer<T> successFn) {
            return getterFn1.get().andOnSuccess(result1 -> {
                successFn.accept(result1);
                return ServiceResult.serviceSuccess();
            });
        }
    }

    /**
     * This class is produced by the find() method, which given 2 ServiceResult suppliers, is able to execute the ServiceResults
     * in a chain and fail early if necessary.  Assuming that they are all successes, a supplied BiFunction can then be called
     * with the 2 successful ServiceResult values as its 2 inputs
     *
     * @param <R>
     * @param <S>
     */
    public static class ServiceResultTuple2Handler<R, S> {

        private Supplier<ServiceResult<R>> getterFn1;
        private Supplier<ServiceResult<S>> getterFn2;

        public ServiceResultTuple2Handler(Supplier<ServiceResult<R>> getterFn1, Supplier<ServiceResult<S>> getterFn2) {
            this.getterFn1 = getterFn1;
            this.getterFn2 = getterFn2;
        }

        public <T> ServiceResult<T> andOnSuccess(BiFunction<R, S, ServiceResult<T>> mainFunction) {
            return getterFn1.get().andOnSuccess(result1 -> getterFn2.get().andOnSuccess(result2 -> mainFunction.apply(result1, result2)));
        }

        public <T> ServiceResult<T> andOnSuccess(Supplier<T> supplier) {
            return andOnSuccess((p1, p2) -> ServiceResult.serviceSuccess(supplier.get()));
        }

        public ServiceResult<Void> andOnSuccessReturnVoid(BiConsumer<R, S> consumer) {
            return andOnSuccess((p1, p2) -> {
                consumer.accept(p1, p2);
                return ServiceResult.serviceSuccess();
            });
        }
    }

    /**
     * This class is produced by the find() method, which given 2 ServiceResult suppliers, is able to execute the ServiceResults
     * in a chain and fail early if necessary.  Assuming that they are all successes, a supplied BiFunction can then be called
     * with the 2 successful ServiceResult values as its 2 inputs
     *
     * @param <R>
     * @param <S>
     */
    public static class ServiceResultTuple3Handler<R, S, T> {

        private Supplier<ServiceResult<R>> getterFn1;
        private Supplier<ServiceResult<S>> getterFn2;
        private Supplier<ServiceResult<T>> getterFn3;

        public ServiceResultTuple3Handler(Supplier<ServiceResult<R>> getterFn1, Supplier<ServiceResult<S>> getterFn2, Supplier<ServiceResult<T>> getterFn3) {
            this.getterFn1 = getterFn1;
            this.getterFn2 = getterFn2;
            this.getterFn3 = getterFn3;
        }

        public <A> ServiceResult<A> andOnSuccess(TriFunction<R, S, T, ServiceResult<A>> mainFunction) {
            return getterFn1.get().
                    andOnSuccess(result1 -> getterFn2.get().
                            andOnSuccess(result2 -> getterFn3.get().
                                    andOnSuccess(result3 -> mainFunction.apply(result1, result2, result3))));
        }

        public <T> ServiceResult<T> andOnSuccess(Supplier<T> supplier) {
            return andOnSuccess((p1, p2, p3) -> ServiceResult.serviceSuccess(supplier.get()));
        }

        public ServiceResult<Void> andOnSuccessReturnVoid(TriConsumer<R, S, T> consumer) {
            return andOnSuccess((p1, p2, p3) -> {
                consumer.accept(p1, p2, p3);
                return ServiceResult.serviceSuccess();
            });
        }
    }

    /**
     * This class is produced by the find() method, which given 2 ServiceResult suppliers, is able to execute the ServiceResults
     * in a chain and fail early if necessary.  Assuming that they are all successes, a supplied BiFunction can then be called
     * with the 2 successful ServiceResult values as its 2 inputs
     *
     * @param <R>
     * @param <S>
     */
    public static class ServiceResultTuple4Handler<R, S, T, U> {

        private Supplier<ServiceResult<R>> getterFn1;
        private Supplier<ServiceResult<S>> getterFn2;
        private Supplier<ServiceResult<T>> getterFn3;
        private Supplier<ServiceResult<U>> getterFn4;

        public ServiceResultTuple4Handler(Supplier<ServiceResult<R>> getterFn1, Supplier<ServiceResult<S>> getterFn2, Supplier<ServiceResult<T>> getterFn3, Supplier<ServiceResult<U>> getterFn4) {
            this.getterFn1 = getterFn1;
            this.getterFn2 = getterFn2;
            this.getterFn3 = getterFn3;
            this.getterFn4 = getterFn4;
        }

        public <A> ServiceResult<A> andOnSuccess(QuadFunction<R, S, T, U, ServiceResult<A>> mainFunction) {
            return getterFn1.get().
                    andOnSuccess(result1 -> getterFn2.get().
                            andOnSuccess(result2 -> getterFn3.get().
                                    andOnSuccess(result3 -> getterFn4.get().
                                            andOnSuccess(result4 -> mainFunction.apply(result1, result2, result3, result4)))));
        }

        public <T> ServiceResult<T> andOnSuccess(Supplier<T> supplier) {
            return andOnSuccess((p1, p2, p3, p4) -> ServiceResult.serviceSuccess(supplier.get()));
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