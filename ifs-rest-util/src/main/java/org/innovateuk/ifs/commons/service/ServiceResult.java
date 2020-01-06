package org.innovateuk.ifs.commons.service;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ErrorTemplate;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.util.Either;
import org.innovateuk.ifs.util.ExceptionThrowingConsumer;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.util.CollectionFunctions.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Represents the result of an action, that will be either a failure or a success.  A failure will result in a ServiceFailure, and a
 * success will result in a T.  Additionally, these can be mapped to produce new ServiceResults that either fail or succeed.
 * Serializable so that it can be persisted in a redis cache.
 */
public class ServiceResult<T> extends BaseFailingOrSucceedingResult<T, ServiceFailure>  implements Serializable {
    private static final Log LOG = LogFactory.getLog(ServiceResult.class);
    private static final long serialVersionUID = -1593842731866554856L;

    private ServiceResult(Either<ServiceFailure, T> result) {
        super(result);
    }

    @Override
    public <R> ServiceResult<R> andOnSuccess(ExceptionThrowingFunction<? super T, FailingOrSucceedingResult<R, ServiceFailure>> successHandler) {
        return (ServiceResult<R>) super.andOnSuccess(successHandler);
    }

    @Override
    public <R> ServiceResult<R> andOnSuccess(Supplier<? extends FailingOrSucceedingResult<R, ServiceFailure>> successHandler) {
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
    public ServiceResult<T> andOnSuccessDo(Consumer<T> successHandler) {
        return (ServiceResult<T>) super.andOnSuccessDo(successHandler);
    }

    @Override
    public <R> ServiceResult<R> andOnFailure(Supplier<FailingOrSucceedingResult<R, ServiceFailure>> failureHandler) {
        return (ServiceResult<R>) super.andOnFailure(failureHandler);
    }

    @Override
    public <R> ServiceResult<R> andOnFailure(Consumer<ServiceFailure> failureHandler) {
        return (ServiceResult<R>) super.andOnFailure(failureHandler);
    }

    @Override
    public <R> ServiceResult<R> andOnFailure(Runnable failureHandler) {
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

    @Override
    public ServiceResult<T> handleSuccessOrFailureNoReturn(ExceptionThrowingConsumer<? super ServiceFailure> failureHandler, ExceptionThrowingConsumer<? super T> successHandler) {
        return (ServiceResult<T>) super.handleSuccessOrFailureNoReturn(failureHandler, successHandler);
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
    protected <R> BaseFailingOrSucceedingResult<R, ServiceFailure> createSuccess(FailingOrSucceedingResult<R, ServiceFailure> success) {
        return serviceSuccess(success.getSuccess());
    }

    @Override
    protected <R> BaseFailingOrSucceedingResult<R, ServiceFailure> createFailure(ServiceFailure failure) {
        return serviceFailure(failure);
    }

    @Override
    protected <R> BaseFailingOrSucceedingResult<R, ServiceFailure> createSuccess(R success) {
        return serviceSuccess(success);
    }

    @Override
    protected <R> BaseFailingOrSucceedingResult<R, ServiceFailure> createFailure(FailingOrSucceedingResult<R, ServiceFailure> failure) {
        return failure != null ? serviceFailure(failure.getFailure()) : serviceFailure(internalServerErrorError());
    }

    /**
     * Switches this ServiceResult to be a success case if we encountered a Not Found that was an acceptable case.
     * Additionally the result is returned as an Optional T rather than a T as the calling code will be assuming that
     * they may or may not be getting a result back from this call.
     *
     * If this ServiceResult is a failure for another reason, the returned ServiceResult will contain the same failures.
     */
    public ServiceResult<Optional<T>> toOptionalIfNotFound() {
        return handleSuccessOrFailure(
                failure -> {
                    if (simpleAllMatch(failure.getErrors(), e -> NOT_FOUND.equals(e.getStatusCode()))) {
                        return serviceSuccess(Optional.empty());
                    } else {
                        return (ServiceResult<Optional<T>>) this;
                    }
                },
                success -> serviceSuccess(getOptionalSuccessObject())
        );
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
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a POST request that has updated
     * data though not at the location POSTED to.
     *
     * This will be a bodiless RestResult with a "200 - OK" response.
     */
    public RestResult<Void> toPostResponse() {
        return handleSuccessOrFailure(failure -> toRestFailure(), success -> RestResult.toPostResponse());
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a POST request that has updated
     * data though not at the location POSTED to.
     *
     * This will be a RestResult with a "200 - OK" response and includes a body.
     */
    public RestResult<T> toPostWithBodyResponse() {
        return handleSuccessOrFailure(failure -> toRestFailure(), RestResult::toPostWithBodyResponse);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ServiceResult<?> that = (ServiceResult<?>) o;

        return new EqualsBuilder()
                .append(result, that.result)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(result)
                .toHashCode();
    }

    /**
     * A factory method to generate a successful ServiceResult without a body.
     */
    public static ServiceResult<Void> serviceSuccess() {
        return new ServiceResult<>(Either.right(null));
    }

    /**
     * A factory method to generate a successful ServiceResult with a body to return.
     */
    public static <T> ServiceResult<T> serviceSuccess(T successfulResult) {
        return new ServiceResult<>(Either.right(successfulResult));
    }

    /**
     * A factory method to generate a failing ServiceResult based upon another.
     */
    public static <T> ServiceResult<T> serviceFailure(ServiceFailure failure) {
        return new ServiceResult<>(Either.left(failure));
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
        return new ServiceResult<>(Either.left(new ServiceFailure(errors)));
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
     * A convenience factory method to take a list of ServiceResults and generate a successful ServiceResult only if
     * all ServiceResults are successful, and return a specific failure in the event that there were any errors detected.
     */
    public static <T, R> ServiceResult<T> processAnyFailuresOrSucceed(List<ServiceResult<R>> results, Function<List<ServiceResult<R>>, ServiceResult<T>> failureResponseFn, ServiceResult<T> successResponse) {
        List<ServiceResult<R>> failures = simpleFilter(results, ServiceResult::isFailure);
        return !failures.isEmpty() ? failureResponseFn.apply(failures) : successResponse;
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
        return BaseFailingOrSucceedingResult.aggregate(
                input,
                (f1, f2) -> new ServiceFailure(combineLists(f1.getErrors(), f2.getErrors())),
                serviceSuccess(emptyList()));
    }

    public static <T> ServiceResult<List<T>> aggregate(final ServiceResult<T>... inputs) {
        List<ServiceResult<T>> input = asList(inputs);
        return aggregate(input);
    }

    /**
     * Aggregate results together to catch failures if any existed.  Upon success, return the same map as above, but "unpack"
     * the individual ServiceResults from the Map values() and envelope the resultant map in an encompassing ServiceResult instead
     *
     * @param mapWithServiceResultLists
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> ServiceResult<Map<K, List<V>>> aggregateMap(Map<K, List<ServiceResult<V>>> mapWithServiceResultLists) {

        ServiceResult<List<V>> overallResults = aggregate(flattenLists(new ArrayList<>(mapWithServiceResultLists.values())));

        return overallResults.andOnSuccessReturn(() -> simpleToMap(mapWithServiceResultLists.entrySet(),
                Map.Entry::getKey, q -> aggregate(q.getValue()).getSuccess()));
    }

    public static <T> HttpStatus findStatusCode(List<ServiceResult<T>> failures) {

        List<Error> aggregateErrors = aggregate(failures).getFailure().getErrors();

        return simpleFindFirst(aggregateErrors, error -> error.getStatusCode() != null).
                map(Error::getStatusCode).
                orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
