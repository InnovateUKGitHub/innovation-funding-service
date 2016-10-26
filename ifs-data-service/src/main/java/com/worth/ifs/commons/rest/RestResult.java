package com.worth.ifs.commons.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.commons.error.CommonFailureKeys;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorTemplate;
import com.worth.ifs.commons.error.exception.*;
import com.worth.ifs.commons.service.BaseEitherBackedResult;
import com.worth.ifs.commons.service.ExceptionThrowingFunction;
import com.worth.ifs.commons.service.FailingOrSucceedingResult;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.util.Either;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.Either.left;
import static com.worth.ifs.util.Either.right;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpStatus.*;

/**
 * Represents the result of a Rest Controller action, that will be either a failure or a success.  A failure will result in a RestFailure, and a
 * success will result in a T.  Additionally, these can be mapped to produce new RestResults that either fail or succeed.
 */
public class RestResult<T> extends BaseEitherBackedResult<T, RestFailure> {

	private static final Log LOG = LogFactory.getLog(RestResult.class);

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
    public <R> RestResult<R> andOnSuccess(Supplier<FailingOrSucceedingResult<R, RestFailure>> successHandler) {
        return (RestResult<R>) super.andOnSuccess(successHandler);
    }

    @Override
    public <R> RestResult<R> andOnSuccessReturn(Supplier<R> successHandler) {
        return (RestResult<R>) super.andOnSuccessReturn(successHandler);
    }

    @Override
    public <R> RestResult<R> andOnSuccess(ExceptionThrowingFunction<? super T, FailingOrSucceedingResult<R, RestFailure>> successHandler) {
        return (RestResult<R>) super.andOnSuccess(successHandler);
    }

    @Override
    public <R> RestResult<R> andOnSuccessReturn(ExceptionThrowingFunction<? super T, R> successHandler) {
        return (RestResult<R>) super.andOnSuccessReturn(successHandler);
    }

    /**
     *
     * @param restFailure - Failure object with details about the failure
     * @return Always returns null
     */
    @Override
    public T findAndThrowException(RestFailure restFailure) {
        final Error error = getMostRelevantErrorForEndUser(restFailure.getErrors());

        if(restFailure.has(GENERAL_NOT_FOUND)){
            throw new ObjectNotFoundException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(GENERAL_FORBIDDEN) || restFailure.has(GENERAL_SPRING_SECURITY_FORBIDDEN_ACTION)){
            throw new ForbiddenActionException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE)){
            throw new UnableToRenderNotificationTemplateException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(GENERAL_UNEXPECTED_ERROR) || restFailure.has(CommonFailureKeys.EMAILS_NOT_SENT_MULTIPLE)){
            throw new GeneralUnexpectedErrorException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE)){
            throw new UnableToSendEmailsException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(FILES_DUPLICATE_FILE_CREATED)){
            throw new DuplicateFileCreatedException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(FILES_FILE_ALREADY_LINKED_TO_FORM_INPUT_RESPONSE)){
            throw new FileAlreadyLinkedToFormInputResponseException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(FILES_INCORRECTLY_REPORTED_FILESIZE)){
            throw new IncorrectlyReportedFileSizeException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(FILES_INCORRECTLY_REPORTED_MEDIA_TYPE)){
            throw new IncorrectlyReportedMediaTypeException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(FILES_UNABLE_TO_CREATE_FILE)){
            throw new UnableToCreateFileException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(FILES_UNABLE_TO_CREATE_FOLDERS)){
            throw new UnableToCreateFoldersException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(FILES_UNABLE_TO_DELETE_FILE)){
            throw new UnableToDeleteFileException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(FILES_UNABLE_TO_UPDATE_FILE)){
            throw new UnableToUpdateFileException(error.getErrorKey(), error.getArguments());
        }

        if (restFailure.has(GENERAL_INCORRECT_TYPE)){
            throw new IncorrectArgumentTypeException(error.getErrorKey(), error.getArguments());
        }

        if (restFailure.has(FILES_FILE_AWAITING_VIRUS_SCAN)){
            throw new FileAwaitingVirusScanException(error.getErrorKey(), error.getArguments());
        }

        if (restFailure.has(FILES_FILE_QUARANTINED)){
            throw new FileQuarantinedException(error.getErrorKey(), error.getArguments());
        }

        if (restFailure.has(USERS_EMAIL_VERIFICATION_TOKEN_NOT_FOUND)) {
            throw new InvalidURLException(error.getErrorKey(), error.getArguments());
        }

        if (restFailure.has(USERS_EMAIL_VERIFICATION_TOKEN_EXPIRED)) {
            throw new RegistrationTokenExpiredException(error.getErrorKey(), error.getArguments());
        }

        if(restFailure.has(BANK_DETAILS_DONT_EXIST_FOR_GIVEN_PROJECT_AND_ORGANISATION)){
            throw new ObjectNotFoundException(error.getErrorKey(), error.getArguments());
        }

        if (restFailure.has(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE)) {
            throw new UnableToAcceptInviteException(error.getErrorKey(), error.getArguments());
        }

        if (restFailure.has(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_ACCEPTED_INVITE)) {
            throw new UnableToAcceptInviteException(error.getErrorKey(), error.getArguments());
        }

        if (restFailure.has(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_REJECTED_INVITE)) {
            throw new UnableToAcceptInviteException(error.getErrorKey(), error.getArguments());
        }

        throw new RuntimeException();
    }

    /* TODO: We need to possibly decide on some sort of precedence.  Only once exception can be thrown and a single
       HTTP error code returned.  So if there are multiple errors then we need to pick the high precedence one.*/
    private Error getMostRelevantErrorForEndUser(final List<Error> errors){
        return errors.get(0);
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

    @Override
    protected <R> BaseEitherBackedResult<R, RestFailure> createFailure(RestFailure failure) {
        return restFailure(failure);
    }

    public HttpStatus getStatusCode() {
        return isFailure() ? result.getLeft().getStatusCode() : successfulStatusCode;
    }

    public ServiceResult<T> toServiceResult() {
        return handleSuccessOrFailure(
                failure -> serviceFailure(getFailure().getErrors()),
                success -> serviceSuccess(success));
    }

    /**
     * Switches this RestResult to be a success case if we encountered a Not Found that was an acceptable case.
     * Additionally the result is returned as an Optional T rather than a T as the calling code will be assuming that
     * they may or may not be getting a result back from this call.
     *
     * If this RestResult is a failure for another reason, the returned RestResult will contain the same failures.
     *
     * @return
     */
    public RestResult<Optional<T>> toOptionalIfNotFound() {
        return handleSuccessOrFailure(
                failure -> {
                    if (NOT_FOUND.equals(failure.getStatusCode())) {
                        return restSuccess(Optional.empty());
                    } else {
                        return (RestResult<Optional<T>>) this;
                    }
                },
                success -> restSuccess(getOptionalSuccessObject())
        );
    }

    public static <T1> T1 getLeftOrRight(Either<T1, T1> either) {
        return Either.getLeftOrRight(either);
    }

    public static RestResult<Void> restSuccess() {
        return restSuccess(OK);
    }

    public static RestResult<Void> restSuccess(HttpStatus statusCode) {
        return restSuccess(null, statusCode);
    }

    public static <T> RestResult<T> restSuccess(T successfulResult) {
        return restSuccess(successfulResult, OK);
    }

    public static <T> RestResult<T> restAccepted(T successfulResult) {
        return restSuccess(successfulResult, ACCEPTED);
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

    public static <T> RestResult<T> restFailure(ErrorTemplate errorTemplate) {
        return restFailure(singletonList(new Error(errorTemplate)));
    }

    public static <T> RestResult<T> restFailure(Error error) {
        return restFailure(singletonList(error));
    }

    public static <T> RestResult<T> restFailure(List<Error> errors) {
        return new RestResult<>(left(RestFailure.error(errors)), HttpStatus.NOT_ACCEPTABLE);
    }

    public static <T> RestResult<T> restFailure(List<Error> errors, HttpStatus statusCode) {
        return new RestResult<>(left(RestFailure.error(errors, statusCode)), null);
    }

    public static <T> Either<Void, T> fromJson(String json, Class<T> clazz) {
        if (Void.class.equals(clazz)) {
            return right(null);
        }
        if (String.class.equals(clazz)) {
            return Either.<Void, T>right((T) json);
        }
        try {
            return right(new ObjectMapper().readValue(json, clazz));
        } catch (IOException e) {
        	LOG.error(e);
            return left();
        }
    }

    public static <T> RestResult<T> fromException(HttpStatusCodeException e) {
        return fromJson(e.getResponseBodyAsString(), RestErrorResponse.class).mapLeftOrRight(
                failure -> RestResult.<T>restFailure(GENERAL_REST_RESULT_UNABLE_TO_PROCESS_REST_ERROR_RESPONSE),
                success -> RestResult.<T>restFailure(success.getErrors(), e.getStatusCode())
        );
    }

    public static <T> RestResult<T> fromResponse(final ResponseEntity<T> response, HttpStatus expectedSuccessCode, HttpStatus... otherExpectedStatusCodes) {
        List<HttpStatus> allExpectedSuccessStatusCodes = combineLists(asList(otherExpectedStatusCodes), expectedSuccessCode);
        System.out.println("Haho fromResponse: allExpected null? " + (allExpectedSuccessStatusCodes==null) + " and response? " + (response==null));
        if (allExpectedSuccessStatusCodes.contains(response.getStatusCode())) {
            return RestResult.<T>restSuccess(response.getBody(), response.getStatusCode());
        } else {
            return RestResult.<T>restFailure(new com.worth.ifs.commons.error.Error(GENERAL_REST_RESULT_UNEXPECTED_STATUS_CODE, response.getStatusCode()));
        }
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a GET request that is requesting
     * data.
     *
     * This will be a RestResult containing the body of the ServiceResult and a "200 - OK" response.
     */
    public static <T> RestResult<T> toGetResponse(T body) {
        return restSuccess(body, OK);
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a POST request that is
     * creating data.
     *
     * This will be a RestResult containing the body of the ServiceResult and a "201 - Created" response.
     *
     * This is an appropriate response for a POST that is creating data.  To update data, consider using a PUT.
     */
    public static <T> RestResult<T> toPostCreateResponse(T body) {
        return restSuccess(body, CREATED);
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a POST request that is
     * accepting data.
     *
     * This will be a RestResult containing the body of the ServiceResult and a "202 - Accepted" response.
     *
     * This is an appropriate response for a POST that is creating data.  To update data, consider using a PUT.
     */
    public static <T> RestResult<T> toPostAcceptResponse(T body) {
        return restSuccess(body, ACCEPTED);
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a POST request that has updated
     * data though not at the location POSTED to.
     *
     * This will be a bodiless RestResult with a "200 - OK" response.
     */
    public static RestResult<Void> toPostResponse() {
        return restSuccess();
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a PUT request that is
     * updating data.
     *
     * This will be a bodiless RestResult with a "200 - OK" response.
     */
    public static RestResult<Void> toPutResponse() {
        return restSuccess();
    }

    /**
     * @deprecated PUTs shouldn't generally return results in their bodies
     *
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a PUT request that is
     * updating data.
     *
     * This will be a RestResult containing the body of the ServiceResult with a "200 - OK" response, although ideally
     * PUT responses shouldn't need to inculde bodies.
     */
    @Deprecated
    public static <T> RestResult<T> toPutWithBodyResponse(T body) {
        return restSuccess(body, OK);
    }

    /**
     * Convenience method to convert a ServiceResult into an appropriate RestResult for a DELETE request that is
     * deleting data.
     *
     * This will be a bodiless RestResult with a "204 - No content" response.
     */
    public static RestResult<Void> toDeleteResponse() {
        return restSuccess(NO_CONTENT);
    }


    /**
     * Aggregate a {@link List} of {@link RestResult} into a {@link RestResult} containing a {@list List}
     *
     * @param input
     * @param <T>
     * @return
     */
    public static <T> RestResult<List<T>> aggregate(final List<RestResult<T>> input) {
        return BaseEitherBackedResult.aggregate(
                input,
                (f1, f2) -> new RestFailure(combineLists(f1.getErrors(), f2.getErrors())),
                restSuccess(emptyList()));
    }


}
