package com.worth.ifs.commons.rest;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.util.Either;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;

/**
 * Represents the result of an action, that will be either a failure or a success.  A failure will result in a RestFailure, and a
 * success will result in a T.  Additionally, these can be mapped to produce new ServiceResults that either fail or succeed.
 */
public class RestResult<T> {

    private Either<RestFailure, RestSuccess<T>> result;
    private boolean bodiless = false;

    protected RestResult(RestResult<T> original) {
        this.result = original.result;
        this.bodiless = original.bodiless;
    }

    private RestResult(RestSuccess<T> success) {
        this(right(success));
    }

    private RestResult(RestFailure failure) {
        this(left(failure));
    }

    private RestResult(Either<RestFailure, RestSuccess<T>> result) {
        this.result = result;
    }

    public <T1> T1 mapLeftOrRight(Function<? super RestFailure, ? extends T1> lFunc, Function<? super RestSuccess<T>, ? extends T1> rFunc) {
        return result.mapLeftOrRight(lFunc, rFunc);
    }

    public <R> RestResult<R> map(Function<? super RestSuccess<T>, RestResult<R>> rFunc) {

        if (result.isLeft()) {
            return restFailure(result.getLeft());
        }

        return rFunc.apply(result.getRight());
    }

    public boolean isLeft() {
        return result.isLeft();
    }

    public boolean isRight() {
        return result.isRight();
    }

    public RestFailure getLeft() {

        if (bodiless) {
            throw new IllegalStateException("Unable to get the body from a bodiless (Void) RestResult");
        }

        return result.getLeft();
    }

    public RestSuccess<T> getRight() {

        if (bodiless) {
            throw new IllegalStateException("Unable to get the body from a bodiless (Void) RestResult");
        }

        return result.getRight();
    }

    public HttpStatus getStatusCode() {
        return mapLeftOrRight(RestFailure::getStatusCode, RestSuccess::getStatusCode);
    }

    public RestResult<Void> bodiless() {
        RestResult<Void> result = mapLeftOrRight((Function<RestFailure, RestResult<Void>>) RestResult::new, success -> new RestResult<>(new RestSuccess("", getStatusCode())));
        result.bodiless = true;
        return result;
    }

    public boolean isBodiless() {
        return bodiless;
    }

    public static <T1> T1 getLeftOrRight(Either<T1, T1> either) {
        return Either.getLeftOrRight(either);
    }

    public static RestResult<Void> restSuccess(HttpStatus statusCode) {
        return restSuccess("", statusCode).bodiless();
    }

    public static <T> RestResult<T> restSuccess(RestSuccess<T> successfulResult) {
        return new RestResult<>(successfulResult);
    }

    public static <T> RestResult<T> restSuccess(T result, HttpStatus statusCode) {
        return new RestResult<>(new RestSuccess<>(result, statusCode));
    }

    public static <T> Supplier<RestResult<T>> successSupplier(RestSuccess<T> successfulResult) {
        return () -> restSuccess(successfulResult);
    }

    public static <T> RestResult<T> restFailure(RestFailure failure) {
        return new RestResult<>(failure);
    }

    public static RestResult<Void> restFailure(HttpStatus statusCode) {
        return restFailure("", statusCode);
    }

    public static <T> RestResult<T> restFailure(Enum<?> failureKey, HttpStatus statusCode) {
        return restFailure(failureKey.name(), statusCode);
    }

    public static <T> RestResult<T> restFailure(Enum<?> failureKey, String failureMessage, HttpStatus statusCode) {
        return new RestResult<>(RestFailure.error(failureKey.name(), failureMessage, statusCode));
    }

    public static <T> RestResult<T> restFailure(String failureMessage, HttpStatus statusCode) {
        return new RestResult<>(RestFailure.error(failureMessage, statusCode));
    }

    public static <T> RestResult<T> restFailure(List<Error> errors) {
        return new RestResult<>(RestFailure.error(errors));
    }
}
