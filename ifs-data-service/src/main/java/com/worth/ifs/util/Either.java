package com.worth.ifs.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
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

    public <T> T andThen(
            Function<? super L, ? extends T> lFunc,
            Function<? super R, ? extends T> rFunc) {
        return left.map(lFunc).orElseGet(() -> right.map(rFunc).get());
    }

    public <T> Either<L, T> andThen(
            Function<? super R, Either<L, T>> rFunc) {
        return isLeft() ? left(left.get()) : rFunc.apply(right.get());
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

    public static <L, R> Supplier<Either<L, R>> toSuppliedLeft(Supplier<L> leftValueSupplier) {
        return () -> left(leftValueSupplier.get());
    }

    public static <L, R> Supplier<Either<L, R>> toRight(R rightValue) {
        return () -> right(rightValue);
    }

    public static <T> T getEither(Either<T, T> either) {
        if (either.isLeft()) {
            return either.getLeft();
        }
        return either.getRight();
    }
}