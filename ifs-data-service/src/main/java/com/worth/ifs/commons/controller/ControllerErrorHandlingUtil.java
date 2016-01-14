package com.worth.ifs.commons.controller;

import com.worth.ifs.transactional.ServiceFailure;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.util.Either;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.worth.ifs.transactional.BaseTransactionalService.Failures.UNEXPECTED_ERROR;
import static com.worth.ifs.transactional.ServiceResult.failure;
import static com.worth.ifs.util.Either.left;
import static java.util.Optional.empty;

/**
 *
 */
public class ControllerErrorHandlingUtil {

    private static final Log LOG = LogFactory.getLog(ControllerErrorHandlingUtil.class);

    public static Optional<JsonStatusResponse> handleServiceFailure(ServiceFailure serviceFailure, List<ServiceFailureToJsonResponseHandler> serviceFailureHandlers, HttpServletResponse response) {

        for (ServiceFailureToJsonResponseHandler handler : serviceFailureHandlers) {
            Optional<JsonStatusResponse> result = handler.handle(serviceFailure, response);
            if (result.isPresent()) {
                return result;
            }
        }

        return empty();
    }

    /**
     * This wrapper wraps the serviceCode function and rolls back transactions upon receiving a ServiceFailure
     * response (an Either with a left of ServiceFailure).
     *
     * It will also catch all exceptions thrown from within serviceCode and convert them into ServiceFailures of
     * type UNEXPECTED_ERROR.
     *
     * @param serviceCode
     * @return
     */
    public static Either<JsonStatusResponse, JsonStatusResponse> handlingErrors(Supplier<JsonStatusResponse> catchAllError, Supplier<Either<JsonStatusResponse, JsonStatusResponse>> serviceCode) {
        try {
            Either<JsonStatusResponse, JsonStatusResponse> response = serviceCode.get();

            if (response.isLeft()) {
                LOG.debug("Controller failure encountered");
            }

            return response;
        } catch (Exception e) {
            LOG.warn("Uncaught exception encountered while performing Controller call - returning catch-all error", e);
            return left(catchAllError.get());
        }
    }

    /**
     * This wrapper wraps the serviceCode function and rolls back transactions upon receiving a ServiceFailure
     * response (an Either with a left of ServiceFailure).
     *
     * It will also catch all exceptions thrown from within serviceCode and convert them into ServiceFailures of
     * type UNEXPECTED_ERROR.
     *
     * @param serviceCode
     * @return
     */
    public static <T> ServiceResult<T> handlingErrors(Enum<?> catchAllError, Supplier<ServiceResult<T>> serviceCode) {
        try {
            ServiceResult<T> response = serviceCode.get();

            if (response.isLeft()) {
                LOG.debug("Controller failure encountered");
            }

            return response;
        } catch (Exception e) {
            LOG.warn("Uncaught exception encountered while performing Controller call - returning catch-all error", e);
            return failure(catchAllError);
        }
    }

    /**
     * This wrapper wraps the serviceCode function and rolls back transactions upon receiving a ServiceFailure
     * response (an Either with a left of ServiceFailure).
     *
     * It will also catch all exceptions thrown from within serviceCode and convert them into ServiceFailures of
     * type UNEXPECTED_ERROR.
     *
     * @param serviceCode
     * @return
     */
    public static <T> ServiceResult<T> handlingErrors(Supplier<ServiceResult<T>> serviceCode) {
        return handlingErrors(UNEXPECTED_ERROR, serviceCode);
    }


    /**
     * This wrapper wraps the serviceCode function, catching all exceptions thrown from within serviceCode and converting them into failing JsonStatusResponses.
     *
     * @param serviceCode
     * @return
     */
    public static Either<ResponseEntity<JsonStatusResponse>, ResponseEntity<?>> handlingErrorsWithResponseEntity(
            Supplier<JsonStatusResponse> catchAllError,
            Supplier<Either<ResponseEntity<JsonStatusResponse>, ResponseEntity<?>>> serviceCode) {

        try {
            Either<ResponseEntity<JsonStatusResponse>, ResponseEntity<?>> response = serviceCode.get();

            if (response.isLeft()) {
                LOG.debug("Controller failure encountered");
            }

            return response;
        } catch (Exception e) {
            LOG.warn("Uncaught exception encountered while performing Controller call - returning catch-all error", e);
            return left(new ResponseEntity<>(catchAllError.get(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

}
