package com.worth.ifs.commons.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorTemplate;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.util.Either;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.util.CollectionFunctions.*;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Represents the result of an action, that will be either a failure or a success.  A failure will result in a ServiceFailure, and a
 * success will result in a T.  Additionally, these can be mapped to produce new ServiceResults that either fail or succeed.
 */
public class ServiceResult<T> extends BaseEitherBackedResult<T, ServiceFailure> {

    private static final Log LOG = LogFactory.getLog(ServiceResult.class);

    private ServiceResult(Either<ServiceFailure, T> result) {
        super(result);
    }

    @Override
    public <R> ServiceResult<R> andOnSuccess(ExceptionThrowingFunction<? super T, FailingOrSucceedingResult<R, ServiceFailure>> successHandler) {
        return (ServiceResult<R>) super.andOnSuccess(successHandler);
    }

    @Override
    public <R> ServiceResult<R> andOnSuccess(Supplier<FailingOrSucceedingResult<R, ServiceFailure>> successHandler) {
        return (ServiceResult<R>) super.andOnSuccess(successHandler);
    }

    @Override
    public <R> ServiceResult<R> andOnSuccessReturn(Supplier<R> successHandler) {
        return (ServiceResult<R>) super.andOnSuccessReturn(successHandler);
    }

    @Override
    public ServiceResult<T> andOnSuccess(Runnable successHandler) {
        return (ServiceResult<T>) super.andOnSuccess(successHandler);
    }

    @Override
    public <R> ServiceResult<R> andOnFailure(Supplier<FailingOrSucceedingResult<R, ServiceFailure>> failureHandler) {
        return (ServiceResult<R>) super.andOnFailure(failureHandler);
    }

    @Override
    public ServiceResult<Void> andOnSuccessReturnVoid(Runnable successHandler) {
        return (ServiceResult<Void>) super.andOnSuccessReturnVoid(successHandler);
    }

    public ServiceResult<Void> andOnSuccessReturnVoid() {
        return andOnSuccess(success -> serviceSuccess());
    }

    public ServiceResult<Void> andOnSuccessReturnVoid(Consumer<? super T> successHandler) {
        return andOnSuccess(success -> {
            successHandler.accept(success);
            return serviceSuccess();
        });
    }

    @Override
    public <R> ServiceResult<R> andOnSuccessReturn(ExceptionThrowingFunction<? super T, R> successHandler) {
        return (ServiceResult<R>) super.andOnSuccessReturn(successHandler);
    }

    /**
     * Not used currently TODO Implement this in future for consistency with RestResult
     *
     * @param serviceFailure - failure object with information about failure
     * @return always returns null
     */
    @Override
    public T findAndThrowException(ServiceFailure serviceFailure) {
        throw new RuntimeException("Was expecting a success object but got a failure.  " + serviceFailure.getErrors());
    }

    @Override
    protected <R> BaseEitherBackedResult<R, ServiceFailure> createSuccess(FailingOrSucceedingResult<R, ServiceFailure> success) {
        return serviceSuccess(success.getSuccessObject());
    }

    @Override
    protected <R> BaseEitherBackedResult<R, ServiceFailure> createFailure(ServiceFailure failure) {
        return serviceFailure(failure);
    }

    @Override
    protected <R> BaseEitherBackedResult<R, ServiceFailure> createSuccess(R success) {
        return serviceSuccess(success);
    }

    @Override
    protected <R> BaseEitherBackedResult<R, ServiceFailure> createFailure(FailingOrSucceedingResult<R, ServiceFailure> failure) {
        return failure != null ? serviceFailure(failure.getFailure()) : serviceFailure(internalServerErrorError());
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a GET request that is requesting
     * data.
     * <p>
     * This will be a RestResult containing the body of the ServiceResult and a "200 - OK" response.
     */
    public RestResult<T> toGetResponse() {
        return handleSuccessOrFailure(failure -> toRestFailure(), RestResult::toGetResponse);
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a POST request that is
     * creating data.
     * <p>
     * This will be a RestResult containing the body of the ServiceResult and a "201 - Created" response.
     * <p>
     * This is an appropriate response for a POST that is creating data.  To update data, consider using a PUT.
     */
    public RestResult<T> toPostCreateResponse() {
        return handleSuccessOrFailure(failure -> toRestFailure(), RestResult::toPostCreateResponse);
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a POST request that is
     * accepting data.
     * <p>
     * This will be a RestResult containing the body of the ServiceResult and a "202 - Accepted" response.
     * <p>
     * This is an appropriate response for a POST that is creating data.  To update data, consider using a PUT.
     */
    public RestResult<T> toPostAcceptResponse() {
        return handleSuccessOrFailure(failure -> toRestFailure(), RestResult::toPostAcceptResponse);
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a POST request that has updated
     * data though not at the location POSTED to.
     *
     * This will be a bodiless RestResult with a "200 - OK" response.
     */
    public RestResult<Void> toPostResponse() {
        return handleSuccessOrFailure(failure -> toRestFailure(), success -> RestResult.toPostResponse());
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a PUT request that is
     * updating data.
     * <p>
     * This will be a bodiless RestResult with a "200 - OK" response.
     */
    public RestResult<Void> toPutResponse() {
        return handleSuccessOrFailure(failure -> toRestFailure(), success -> RestResult.toPutResponse());
    }

    /**
     * @deprecated PUTs shouldn't generally return results in their bodies
     * <p>
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a PUT request that is
     * updating data.
     * <p>
     * This will be a RestResult containing the body of the ServiceResult with a "200 - OK" response, although ideally
     * PUT responses shouldn't need to inculde bodies.
     */
    @Deprecated
    public RestResult<T> toPutWithBodyResponse() {
        return handleSuccessOrFailure(failure -> toRestFailure(), RestResult::toPutWithBodyResponse);
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a DELETE request that is
     * deleting data.
     * <p>
     * This will be a bodiless RestResult with a "204 - No content" response.
     */
    public RestResult<Void> toDeleteResponse() {
        return handleSuccessOrFailure(failure -> toRestFailure(), success -> RestResult.toDeleteResponse());
    }

    private <T> RestResult<T> toRestFailure() {
        return restFailure(getFailure().getErrors());
    }

    /**
     * A factory method to generate a successful ServiceResult without a body.
     */
    public static ServiceResult<Void> serviceSuccess() {
        return new ServiceResult<>(right(null));
    }

    /**
     * A factory method to generate a successful ServiceResult with a body to return.
     */
    public static <T> ServiceResult<T> serviceSuccess(T successfulResult) {
        return new ServiceResult<>(right(successfulResult));
    }

    /**
     * A factory method to generate a failing ServiceResult based upon another.
     */
    public static <T> ServiceResult<T> serviceFailure(ServiceFailure failure) {
        return new ServiceResult<>(left(failure));
    }

    /**
     * A factory method to generate a failing ServiceResult based upon an Error.
     */
    public static <T> ServiceResult<T> serviceFailure(Error error) {
        return serviceFailure(singletonList(error));
    }

    /**
     * A factory method to generate a failing ServiceResult based upon an ErrorTemplate.
     */
    public static <T> ServiceResult<T> serviceFailure(ErrorTemplate errorTemplate) {
        return serviceFailure(singletonList(new Error(errorTemplate)));
    }

    /**
     * A factory method to generate a failing ServiceResult based upon an Error.
     */
    public static <T> ServiceResult<T> serviceFailure(List<Error> errors) {
        return new ServiceResult<>(left(new ServiceFailure(errors)));
    }

    /**
     * A convenience factory method to generate a successful ServiceResult, only if "value" is non null.
     */
    public static <T> ServiceResult<T> getNonNullValue(T value, Error error) {

        if (value == null) {
            return serviceFailure(error);
        }

        return serviceSuccess(value);
    }

    /**
     * A convenience factory method to take a list of ServiceResults and generate a successful ServiceResult only if
     * all ServiceResults are successful.  In the event of a failure, all encountered failing ServiceResults' Errors will
     * be combined into a single failing ServiceResult
     */
    @SafeVarargs
    public static <T> ServiceResult<Void> processAnyFailuresOrSucceed(ServiceResult<T>... results) {
        return processAnyFailuresOrSucceed(asList(results));
    }

    /**
     * A convenience factory method to take a list of ServiceResults and generate a successful ServiceResult only if
     * all ServiceResults are successful.  In the event of a failure, all encountered failing ServiceResults' Errors will
     * be combined into a single failing ServiceResult
     */
    public static <T> ServiceResult<Void> processAnyFailuresOrSucceed(List<ServiceResult<T>> results) {
        List<ServiceResult<T>> failures = simpleFilter(results, ServiceResult::isFailure);

        if (failures.isEmpty()) {
            return serviceSuccess();
        }

        List<List<Error>> errorLists = simpleMap(failures, failure -> failure.getFailure().getErrors());
        List<Error> combinedErrors = flattenLists(errorLists);
        return serviceFailure(combinedErrors);
    }

    /**
     * A convenience factory method to take a list of ServiceResults and generate a successful ServiceResult only if
     * all ServiceResults are successful.  In the event of a failure, all encountered failing ServiceResults' Errors will
     * be combined into a single failing ServiceResult
     */
    public static <T, R> ServiceResult<T> processAnyFailuresOrSucceed(List<ServiceResult<R>> results, ServiceResult<T> successResponse) {
        List<ServiceResult<R>> failures = simpleFilter(results, ServiceResult::isFailure);

        if (failures.isEmpty()) {
            return successResponse;
        }

        List<List<Error>> errorLists = simpleMap(failures, failure -> failure.getFailure().getErrors());
        List<Error> combinedErrors = flattenLists(errorLists);
        return serviceFailure(combinedErrors);
    }


    /**
     * A convenience factory method to take a list of ServiceResults and generate a successful ServiceResult only if
     * all ServiceResults are successful, and return a specific failure in the event that there were any errors detected.
     */
    public static <T, R> ServiceResult<T> processAnyFailuresOrSucceed(List<ServiceResult<R>> results, ServiceResult<T> failureResponse, ServiceResult<T> successResponse) {
        return results.stream().anyMatch(ServiceResult::isFailure) ? failureResponse : successResponse;
    }

    /**
     * This wrapper wraps the serviceCode function and rolls back transactions upon receiving a ServiceFailure
     * response (an Either with a left of ServiceFailure).
     * <p>
     * It will also catch all exceptions thrown from within serviceCode and convert them into ServiceFailures of
     * type GENERAL_UNEXPECTED_ERROR.
     *
     * @param serviceCode - code that performs some process and returns a successful or failing ServiceResult, the state
     *                    of which then allows this wrapper to perform additional actions
     * @param <T>         - the successful return type of the ServiceResult
     * @return the original ServiceResult returned from the serviceCode, or a generic ServiceResult failure if an exception
     * was thrown in serviceCode
     */
    public static <T> ServiceResult<T> handlingErrors(Supplier<ServiceResult<T>> serviceCode) {
        return handlingErrors(internalServerErrorError(), serviceCode);
    }

    /**
     * This wrapper wraps the serviceCode function and rolls back transactions upon receiving a ServiceFailure
     * response (an Either with a left of ServiceFailure).
     * <p>
     * It will also catch all exceptions thrown from within serviceCode and convert them into ServiceFailures of
     * type GENERAL_UNEXPECTED_ERROR.
     *
     * @param serviceCode - code that performs some process and returns a successful or failing ServiceResult, the state
     *                    of which then allows this wrapper to perform additional actions
     * @param <T>         - the successful return type of the ServiceResult
     * @return the original ServiceResult returned from the serviceCode, or a generic ServiceResult failure if an exception
     * was thrown in serviceCode
     */
    public static <T> ServiceResult<T> handlingErrors(ErrorTemplate catchAllErrorTemplate, Supplier<ServiceResult<T>> serviceCode) {
        return handlingErrors(new Error(catchAllErrorTemplate), serviceCode);
    }

    /**
     * This wrapper wraps the serviceCode function and rolls back transactions upon receiving a ServiceFailure
     * response (an Either with a left of ServiceFailure).
     * <p>
     * It will also catch all exceptions thrown from within serviceCode and convert them into ServiceFailures of
     * type GENERAL_UNEXPECTED_ERROR.
     *
     * @param <T>         - the successful return type of the ServiceResult
     * @param serviceCode - code that performs some process and returns a successful or failing ServiceResult, the state
     *                    of which then allows this wrapper to perform additional actions
     * @return the original ServiceResult returned from the serviceCode, or a generic ServiceResult failure if an exception
     * was thrown in serviceCode
     */
    public static <T> ServiceResult<T> handlingErrors(Error catchAllError, Supplier<ServiceResult<T>> serviceCode) {
        try {
            return serviceCode.get();
        } catch (Exception e) {
            LOG.warn("Uncaught exception encountered while performing service call.  Returning ServiceFailure", e);
            return serviceFailure(catchAllError);
        }
    }


    /**
     * Aggregate a {@link List} of {@link ServiceResult} into a {@link ServiceResult} containing a {@list List}
     * @param input
     * @param <T>
     * @return
     */
    public static <T> ServiceResult<List<T>> aggregate(final List<ServiceResult<T>> input) {
        return BaseEitherBackedResult.aggregate(
                input,
                (f1, f2) -> new ServiceFailure(combineLists(f1.getErrors(), f2.getErrors())),
                serviceSuccess(emptyList()));
    }

    public static <T> ServiceResult<List<T>> aggregate(final ServiceResult<T>... inputs) {
        List<ServiceResult<T>> input = asList(inputs);
        return aggregate(input);
    }



}
