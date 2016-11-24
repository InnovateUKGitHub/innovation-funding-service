package com.worth.ifs.util;

import com.worth.ifs.commons.service.ExceptionThrowingFunction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * This class represents a return type that can have 2 possible return types, either a "left" value or a "right"
 * value.  Typically in functional programming, an Either is used to represent success and failure cases, where
 * the "left" type is the failure type and the "right" type is the success type.  Either producers can then be
 * chained together using the {@link Either#mapLeftOrRight(ExceptionThrowingFunction, ExceptionThrowingFunction)} method
 * so that the if a "left" value is encountered during the execution of the chain, the processing will "short circuit"
 * and return the left response without evaluating any further Either producers in the chain.
 */
public class Either<L, R> {

    private static final Log LOG = LogFactory.getLog(Either.class);

    private boolean leftSet = false;
    private boolean rightSet = false;

    public static <R> Either<Void, R> left() {
        Either<Void, R> leftEither = new Either<>(null, null);
        leftEither.leftSet = true;
        return leftEither;
    }

    public static <L, R> Either<L, R> left(L value) {
        Either<L, R> leftEither = new Either<>(value, null);
        leftEither.leftSet = true;
        return leftEither;
    }

    public static <L, R> Either<L, R> right(R value) {
        Either<L, R> rightEither = new Either<>(null, value);
        rightEither.rightSet = true;
        return rightEither;
    }

    private final L left;
    private final R right;

    protected Either(L l, R r) {
        left = l;
        right = r;
    }

    public <T> T mapLeftOrRight(
            ExceptionThrowingFunction<? super L, ? extends T> lFunc,
            ExceptionThrowingFunction<? super R, ? extends T> rFunc) {
        try {
            return isLeft() ? lFunc.apply(left) : rFunc.apply(right);
        } catch (Throwable e) {
            LOG.warn("Exception caught while processing function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public <T> Either<L, T> map(
            ExceptionThrowingFunction<? super R, Either<L, T>> rFunc) {
        try {
            return isLeft() ? left(left) : rFunc.apply(right);
        } catch (Throwable e) {
            LOG.warn("Exception caught while processing function - throwing as a runtime exception", e);
            throw new RuntimeException(e);
        }
    }

    public boolean isLeft() {
        checkEitherState();
        return leftSet;
    }

    private void checkEitherState() {
        if (leftSet && rightSet) {
            throw new IllegalStateException("Illegal state of Either - both left and right present");
        }
        if (!leftSet && !rightSet) {
            throw new IllegalStateException("Illegal state of Either - both left and right not present");
        }
    }

    public boolean isRight() {
        checkEitherState();
        return rightSet;
    }

    public L getLeft() {
        checkEitherState();

        if (!isLeft()) {
            throw new NoSuchElementException("Not a left value.  Right value is  " + getRight());
        }

        return left;
    }

    public R getRight() {
        checkEitherState();

        if (!isRight()) {
            throw new NoSuchElementException("Not a right value.  Left value is " + getLeft());
        }

        return right;
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