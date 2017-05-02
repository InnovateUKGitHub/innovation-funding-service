package org.innovateuk.ifs.shibboleth.api.endpoints;

import org.innovateuk.ifs.shibboleth.api.exceptions.DuplicateEmailException;
import org.innovateuk.ifs.shibboleth.api.exceptions.InvalidPasswordException;
import org.innovateuk.ifs.shibboleth.api.exceptions.PasswordPolicyException;
import org.innovateuk.ifs.shibboleth.api.models.ErrorResponse;
import org.innovateuk.ifs.shibboleth.api.models.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface RestExceptionHandlers {

    @ExceptionHandler(DuplicateEmailException.class)
    default ResponseEntity<List<ErrorResponse>> handleDuplicateIdentityException(final DuplicateEmailException exception) {

        final Logger logger = LoggerFactory.getLogger(RestExceptionHandlers.class);
        logger.debug("Duplicate Identity: ", exception);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonList(exception.toErrorResponse()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    default ResponseEntity<List<ErrorResponse>> handleInvalidPasswordException(final InvalidPasswordException exception) {

        final Logger logger = LoggerFactory.getLogger(RestExceptionHandlers.class);
        logger.debug("Invalid Password: ", exception);

        return ResponseEntity.badRequest().body(Collections.singletonList(exception.toErrorResponse()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    default ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableExceptions(final HttpServletRequest request,
        final HttpMessageNotReadableException exception) {

        final Logger logger = LoggerFactory.getLogger(RestExceptionHandlers.class);
        logger.warn("Invalid request body on [{}] for [{}]", request.getRequestURL(), exception.getMessage());
        logger.debug("Invalid request body - stacktrace.", exception);

        return ResponseEntity.unprocessableEntity().body(new ExceptionResponse(exception));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    default ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValidExceptions(final HttpServletRequest request,
        final MethodArgumentNotValidException exception) {

        final Logger logger = LoggerFactory.getLogger(RestExceptionHandlers.class);
        logger.warn("Invalid method arguments on [{}] for [{}]", request.getRequestURL(), exception.getMessage());
        logger.debug("Invalid method arguments - stacktrace.", exception);

        // TODO - Verify this generates correct JSON response

        final List<ErrorResponse> errorMessages = exception.
            getBindingResult().
            getAllErrors().
            stream().
            map(
                ObjectError::getDefaultMessage
            ).
            map(
                ErrorResponse::new
            ).
            collect(
                Collectors.toList()
            );

        return ResponseEntity.badRequest().body(errorMessages);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    default ResponseEntity<List<ErrorResponse>> handleMethodArgumentTypeMismatchExceptions(
        final HttpServletRequest request,
        final MethodArgumentTypeMismatchException exception) {

        final Logger logger = LoggerFactory.getLogger(RestExceptionHandlers.class);
        logger.warn("Invalid method argument type on [{}] for [{}]", request.getRequestURL(), exception.getMessage());
        logger.debug("Invalid method arguments type - stacktrace.", exception);

        // TODO - Verify this generates correct JSON response

        final List<String> arguments = Arrays.asList(
            exception.getName(),
            exception.getParameter().getParameterName(),
            exception.getRequiredType().getSimpleName(),
            String.valueOf(exception.getValue())
        );

        final List<ErrorResponse> errorMessages = Collections.singletonList(
            new ErrorResponse("INVALID_PARAMETER_TYPE", arguments)
        );

        return ResponseEntity.badRequest().body(errorMessages);
    }

    @ExceptionHandler(PasswordPolicyException.class)
    default ResponseEntity<List<ErrorResponse>> handlePasswordPolicyException(final PasswordPolicyException exception) {

        final Logger logger = LoggerFactory.getLogger(RestExceptionHandlers.class);
        logger.debug("Password policy failure: ", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList(exception.toErrorResponse()));
    }

}
