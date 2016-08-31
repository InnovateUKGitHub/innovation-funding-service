package com.worth.ifs.rest;

import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.commons.rest.ValidationMessages;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

/**
 * Error Controller Advice that is used primarily to catch and deal with Binding Exceptions during JSR-303 binding for the
 * Controller parameters
 */
@ControllerAdvice
public class ErrorControllerAdvice {

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public @ResponseBody RestErrorResponse bindException(MethodArgumentNotValidException ex) {

        BindingResult bindingResult = ex.getBindingResult();
        ValidationMessages validationMessages = new ValidationMessages(bindingResult);
        return new RestErrorResponse(validationMessages);
    }
}

