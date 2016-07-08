package com.worth.ifs.rest;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
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
        List<Error> fieldErrors = simpleMap(bindingResult.getFieldErrors(), e -> fieldError(e.getField(), e.getCode()));
        List<Error> globalErrors = simpleMap(bindingResult.getGlobalErrors(), e -> new Error(e.getCode(), e.getDefaultMessage(), NOT_ACCEPTABLE));
        return new RestErrorResponse(combineLists(fieldErrors, globalErrors));
    }
}

