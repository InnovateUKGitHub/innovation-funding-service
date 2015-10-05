package com.worth.ifs.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by dwatson on 01/10/15.
 */
public class IfsFunctions {

    /**
     * Checks to see whether or not the given request parameter is present and if so, returns a non-empty Optional upon which
     * an "ifPresent" call can be chained
     *
     * @param parameterName
     * @param request
     * @return
     */
    public static Optional<Boolean> requestParameterPresent(String parameterName, HttpServletRequest request) {
        List<String> parameterNames = Collections.list(request.getParameterNames());
        if (parameterNames.stream().anyMatch(name -> name.equals(parameterName))) {
            return Optional.of(true);
        }

        return Optional.empty();
    }


    /**
     * A class to allow chaining of an "else" function on unsuccessful "ifPresent"
     *
     * @param <T>
     */
    public static class IfPresentElse<T> {

        private final boolean wasPresent;
        private final T wasPresentResult;

        public IfPresentElse(boolean wasPresent, T wasPresentResult) {
            this.wasPresent = wasPresent;
            this.wasPresentResult = wasPresentResult;
        }

        public <R> Either<R, T> orElseOther(Supplier<R> elseFunction) {
            if (wasPresent) {
                return Either.right(wasPresentResult);
            }
            return Either.left(elseFunction != null ? elseFunction.get() : null);
        }

        public T orElse(Supplier<T> elseFunction) {
            if (wasPresent) {
                return wasPresentResult;
            }
            return elseFunction != null ? elseFunction.get() : null;
        }
    }


    /**
     * An ifPresent method that allows return values, unlike Optional's ifPresent.  Will return an IfPresentElse which a "orElse"
     * call can be supplied to in the event that the ifPresent test is false
     *
     * @param optional
     * @param ifPresentFunction
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> IfPresentElse<T> ifPresent(Optional<R> optional, Function<R, T> ifPresentFunction) {

        if (optional.isPresent()) {
            T result = ifPresentFunction.apply(optional.get());
            return new IfPresentElse<>(true, result);
        }

        return new IfPresentElse(false, null);
    }

//    /**
//     * A class to allow chaining of an "else" function on unsuccessful "ifPresent"
//     *
//     * @param <T>
//     */
//    public static class IfPresentElseWithFailure<R, T> {
//
//        private final boolean wasPresent;
//        private final Either<R, T> wasPresentResult;
//
//        public IfPresentElseWithFailure(boolean wasPresent, Either<R, T> wasPresentResult) {
//            this.wasPresent = wasPresent;
//            this.wasPresentResult = wasPresentResult;
//        }
//
//        public Either<R, T> orElse(Supplier<Either<R, T>> elseFunction) {
//            if (wasPresent) {
//                return wasPresentResult;
//            }
//            return elseFunction != null ? elseFunction.get() : null;
//        }
//

//        public Either<R, T> orElseOther(Supplier<Either<R, T>> elseFunction) {
//            if (wasPresent && wasPresentResult != null && wasPresentResult.isRight()) {
//                return Either.right(wasPresentResult.getRight());
//            }
//            return elseFunction != null ? elseFunction.get() : null;
//        }
//    }
//
//
//    /**
//     * An ifPresent method that allows return values, unlike Optional's ifPresent.  Will return an IfPresentElse which a "orElse"
//     * call can be supplied to in the event that the ifPresent test is false
//     *
//     * @param optional
//     * @param ifPresentFunction
//     * @param <T>
//     * @param <R>
//     * @return
//     */
//    public static <T, S, R> IfPresentElseWithFailure<S, T> ifPresentWithFailures(Optional<R> optional, Function<R, Either<S, T>> ifPresentFunction) {
//
//        if (optional.isPresent()) {
//            Either<S, T> result = ifPresentFunction.apply(optional.get());
//            return new IfPresentElseWithFailure<S, T>(true, result);
//        }
//
//        return new IfPresentElseWithFailure<>(false, null);
//    }


    public static final class Either<L, R> {
        public static <L, R> Either<L, R> left(L value) {
            return new Either<>(Optional.of(value), Optional.empty());
        }

        public static <L, R> Either<L, R> right(R value) {
            return new Either<>(Optional.empty(), Optional.of(value));
        }

        private final Optional<L> left;
        private final Optional<R> right;

        private Either(Optional<L> l, Optional<R> r) {
            left = l;
            right = r;
        }

        public <T> T map(
                Function<? super L, ? extends T> lFunc,
                Function<? super R, ? extends T> rFunc) {
            return left.map(lFunc).orElseGet(() -> right.map(rFunc).get());
        }

        public <T> Either<T, R> mapLeft(Function<? super L, ? extends T> lFunc) {
            return new Either<>(left.map(lFunc), right);
        }

        public <T> Either<L, T> mapRight(Function<? super R, ? extends T> rFunc) {
            return new Either<>(left, right.map(rFunc));
        }

        public void apply(Consumer<? super L> lFunc, Consumer<? super R> rFunc) {
            left.ifPresent(lFunc);
            right.ifPresent(rFunc);
        }

        public boolean isLeft() {
            checkEitherState();
            return left.isPresent();
        }

        private void checkEitherState() {
            if (left.isPresent() && right.isPresent()) {
                throw new IllegalStateException("Illegal state of Either - both left and right present");
            }
            if (!left.isPresent() && !right.isPresent()) {
                throw new IllegalStateException("Illegal state of Either - both left and right not present");
            }
        }

        public boolean isRight() {
            checkEitherState();
            return right.isPresent();
        }

        public L getLeft() {
            checkEitherState();
            return left.get();
        }

        public R getRight() {
            checkEitherState();
            return right.get();
        }

        public static <L, R> Supplier<Either<L, R>> toLeft(L leftValue) {
            return () -> left(leftValue);
        }

        public static <L, R> Supplier<Either<L, R>> toRight(R rightValue) {
            return () -> right(rightValue);
        }
    }

    public static <T> T getEither(Either<T, T> either) {
        if (either.isLeft()) {
            return either.getLeft();
        }
        return either.getRight();
    }

    public static class FunctionChains {

        public static <A, B, Z> Function<A, Either<Z, B>> chainEithers(Function<A, Either<Z, B>> f1) {
            return a -> f1.apply(a);
        }

        public static <A, B, C, Z> Function<A, Either<Z, C>> chainEithers(Function<A, Either<Z, B>> f1, Function<Either<Z, B>, Either<Z, C>> f2) {
            return f1.andThen(f2);
        }

        public static <A, B, C, D, Z> Function<A, Either<Z, D>> chainEithers(Function<A, Either<Z, B>> f1, Function<Either<Z, B>, Either<Z, C>> f2, Function<Either<Z, C>, Either<Z, D>> f3) {
            return f1.andThen(f2).andThen(f3);
        }

        public static <A, B, Z> Function<Either<Z, A>, Either<Z, B>> chained(Function<A, Either<Z, B>> fn) {
            Function<Either<Z, A>, Either<Z, B>> wrapped = either -> {
                return either.isLeft() ? Either.left(either.getLeft()) : fn.apply(either.getRight());
            };

            return wrapped;
        }
    }
}
