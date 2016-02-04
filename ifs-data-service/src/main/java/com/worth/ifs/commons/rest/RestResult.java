package com.worth.ifs.commons.rest;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.FailingOrSucceedingResult;
import com.worth.ifs.util.Either;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.function.Function;

import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.OK;

/**
 * Represents the result of a Rest Controller action, that will be either a failure or a success.  A failure will result in a RestFailure, and a
 * success will result in a T.  Additionally, these can be mapped to produce new RestResults that either fail or succeed.
 */
public class RestResult<T> implements FailingOrSucceedingResult<T, RestFailure> {

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

    public <T1> T1 handleSuccessOrFailure(Function<? super RestFailure, ? extends T1> failureHandler, Function<? super T, ? extends T1> successHandler) {
        return mapLeftOrRight(failureHandler, successHandler);
    }

    public <R> RestResult<R> andOnSuccess(Function<? super T, FailingOrSucceedingResult<R, RestFailure>> successHandler) {
        return map(successHandler);
    }

    public boolean isFailure() {
        return isLeft();
    }

    public boolean isSuccess() {
        return isRight();
    }

    public RestFailure getFailure() {
        return getLeft();
    }

    public T getSuccessObject() {
        return getRight().getResult();
    }

    public HttpStatus getStatusCode() {
        return isLeft() ? result.getLeft().getStatusCode() : result.getRight().getStatusCode();
    }

    private <T1> T1 mapLeftOrRight(Function<? super RestFailure, ? extends T1> lFunc, Function<? super T, ? extends T1> rFunc) {
        return result.mapLeftOrRight(lFunc, restSuccess -> rFunc.apply(restSuccess.getResult()));
    }

    private <R> RestResult<R> map(Function<? super T, FailingOrSucceedingResult<R, RestFailure>> rFunc) {

        if (result.isLeft()) {
            return restFailure(result.getLeft());
        }

        FailingOrSucceedingResult<R, RestFailure> successResult = rFunc.apply(result.getRight().getResult());

        return successResult.handleSuccessOrFailure(
                failure -> RestResult.<R> restFailure(failure),
                success -> RestResult.<R> restSuccess(success, success instanceof RestResult ? ((RestResult) success).getStatusCode() : OK)
        );
    }

    private boolean isLeft() {
        return result.isLeft();
    }

    private boolean isRight() {
        return result.isRight();
    }

    private RestFailure getLeft() {

        if (bodiless) {
            throw new IllegalStateException("Unable to get the body from a bodiless (Void) RestResult");
        }

        return result.getLeft();
    }

    private RestSuccess<T> getRight() {

        if (bodiless) {
            throw new IllegalStateException("Unable to get the body from a bodiless (Void) RestResult");
        }

        return result.getRight();
    }

    private RestResult<Void> bodiless() {
        RestResult<Void> result = handleSuccessOrFailure(
                failure -> new RestResult<Void>(failure),
                success -> new RestResult<Void>(new RestSuccess<Void>(null, getStatusCode())));
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

    public static <T> RestResult<T> restFailure(Error error) {
        return restFailure(singletonList(error));
    }

    public static <T> RestResult<T> restFailure(List<Error> errors) {
        return new RestResult<>(RestFailure.error(errors));
    }

    public static <T> RestResult<T> restFailure(List<Error> errors, HttpStatus statusCode) {
        return new RestResult<>(RestFailure.error(errors, statusCode));
    }
}
