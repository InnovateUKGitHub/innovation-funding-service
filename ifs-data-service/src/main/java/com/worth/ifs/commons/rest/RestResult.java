package com.worth.ifs.commons.rest;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.BaseEitherBackedResult;
import com.worth.ifs.commons.service.ExceptionThrowingFunction;
import com.worth.ifs.commons.service.FailingOrSucceedingResult;
import com.worth.ifs.util.Either;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

/**
 * Represents the result of a Rest Controller action, that will be either a failure or a success.  A failure will result in a RestFailure, and a
 * success will result in a T.  Additionally, these can be mapped to produce new RestResults that either fail or succeed.
 */
public class RestResult<T> extends BaseEitherBackedResult<T, RestFailure> {

    private HttpStatus successfulStatusCode;

    public RestResult(RestResult<T> original) {
        super(original);
        this.successfulStatusCode = original.successfulStatusCode;
    }

    public RestResult(Either<RestFailure, T> result, HttpStatus successfulStatusCode) {
        super(result);
        this.successfulStatusCode = successfulStatusCode;
    }

    @Override
    public <R> RestResult<R> andOnSuccess(ExceptionThrowingFunction<? super T, FailingOrSucceedingResult<R, RestFailure>> successHandler) {
        return (RestResult<R>) super.andOnSuccess(successHandler);
    }

    @Override
    public <R> RestResult<R> andOnSuccessReturn(ExceptionThrowingFunction<? super T, R> successHandler) {
        return (RestResult<R>) super.andOnSuccessReturn(successHandler);
    }

    @Override
    protected <R> RestResult<R> createSuccess(FailingOrSucceedingResult<R, RestFailure> success) {

        if (success instanceof RestResult) {
            return new RestResult<>((RestResult<R>) success);
        }

        return restSuccess(success.getSuccessObject());
    }

    @Override
    protected <R> RestResult<R> createSuccess(R success) {
        return restSuccess(success);
    }

    @Override
    protected <R> RestResult<R> createFailure(FailingOrSucceedingResult<R, RestFailure> failure) {

        if (failure instanceof RestResult) {
            return new RestResult<>((RestResult<R>) failure);
        }

        return (RestResult<R>) restFailure(INTERNAL_SERVER_ERROR);
    }

    public HttpStatus getStatusCode() {
        return isFailure() ? result.getLeft().getStatusCode() : successfulStatusCode;
    }

    public static <T1> T1 getLeftOrRight(Either<T1, T1> either) {
        return Either.getLeftOrRight(either);
    }

    public static RestResult<Void> restSuccess(HttpStatus statusCode) {
        return restSuccess(null, statusCode);
    }

    public static <T> RestResult<T> restSuccess(T successfulResult) {
        return restSuccess(successfulResult, OK);
    }

    public static <T> RestResult<T> restSuccess(T result, HttpStatus statusCode) {
        return new RestResult<>(right(result), statusCode);
    }

    public static <T> RestResult<T> restFailure(RestFailure failure) {
        return new RestResult<>(left(failure), null);
    }

    public static RestResult<Void> restFailure(HttpStatus statusCode) {
        return restFailure(null, statusCode);
    }

    public static <T> RestResult<T> restFailure(Error error) {
        return restFailure(singletonList(error));
    }

    public static <T> RestResult<T> restFailure(List<Error> errors) {
        return new RestResult<>(left(RestFailure.error(errors)), null);
    }

    public static <T> RestResult<T> restFailure(List<Error> errors, HttpStatus statusCode) {
        return new RestResult<>(left(RestFailure.error(errors, statusCode)), null);
    }
}
