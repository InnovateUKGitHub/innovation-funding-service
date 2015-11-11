package com.worth.ifs.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class represents a return type that can have 2 possible return types, either a "left" value or a "right"
 * value.  Typically in functional programming, an Either is used to represent success and failure cases, where
 * the "left" type is the failure type and the "right" type is the success type.  Either producers can then be
 * chained together using the {@link Either#mapLeftOrRight(Function, Function)} method so that the if a "left" value is
 * encountered during the execution of the chain, the processing will "short circuit" and return the left response
 * without evaluating any further Either producers in the chain.
 *
 * Created by dwatson on 05/10/15.
 */
public class Either<L, R> {

    public static <L, R> Either<L, R> left(L value) {
        return new Either<>(Optional.of(value), Optional.empty());
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Either<>(Optional.empty(), Optional.of(value));
    }

    private final Optional<L> left;
    private final Optional<R> right;

    protected Either(Optional<L> l, Optional<R> r) {
        left = l;
        right = r;
    }

    public <T> T mapLeftOrRight(
            Function<? super L, ? extends T> lFunc,
            Function<? super R, ? extends T> rFunc) {
        return left.map(lFunc).orElseGet(() -> right.map(rFunc).get());
    }

    public <T> Either<L, T> map(
            Function<? super R, Either<L, T>> rFunc) {
        return isLeft() ? left(left.get()) : rFunc.apply(right.get());
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

    public static <L, R> Supplier<Either<L, R>> toSuppliedLeft(Supplier<L> leftValueSupplier) {
        return () -> left(leftValueSupplier.get());
    }

    public static <T> T getLeftOrRight(Either<T, T> either) {
        if (either.isLeft()) {
            return either.getLeft();
        }
        return either.getRight();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Either<?, ?> either = (Either<?, ?>) o;

        if (left != null ? !left.equals(either.left) : either.left != null) return false;
        return !(right != null ? !right.equals(either.right) : either.right != null);

    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}