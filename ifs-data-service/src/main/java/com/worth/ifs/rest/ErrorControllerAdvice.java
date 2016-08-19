package com.worth.ifs.rest;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.error.Error.globalError;
import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
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

        List<Error> fieldErrors = simpleMap(bindingResult.getFieldErrors(), e -> fieldError(e.getField(), e.getRejectedValue(),
                getErrorKeyFromBindingError(e), getArgumentsFromBindingError(e)));

        List<Error> globalErrors = simpleMap(bindingResult.getGlobalErrors(), e -> globalError(getErrorKeyFromBindingError(e),
                getArgumentsFromBindingError(e)));

        return new RestErrorResponse(combineLists(fieldErrors, globalErrors));
    }

    // The Binding Errors in the API contain error keys which can be looked up in the web layer (or used in another client
    // of this API) to produce plain english messages, but it is not the responsibility of the API to produce plain
    // english messages.  Therefore, the "defaultMessage" value in these errors is actually the key that is needed by
    // the web layer to produce the appropriate messages e.g. "validation.standard.email.length.max".
    //
    // The format we receive these in at this point is "{validation.standard.email.length.max}", so we need to ensure
    // that the curly brackets are stripped off before returning to the web layer
    private String getErrorKeyFromBindingError(ObjectError e) {

        String messageKey = e.getDefaultMessage();

        if (messageKey.startsWith("{") && messageKey.endsWith("}")) {
            return messageKey.substring(1, messageKey.length() - 1);
        }

        return messageKey;
    }

    //
    // The arguments provided by the Binding Errors here include as their first argument a version of all the error message
    // information itself stored as a MessageSourceResolvable.  We don't want to be sending this across to the web layer
    // as it's useless information.  We also can't really filter it out entirely because the resource bundle entries in the
    // web layer expect the useful arguments from a Binding Error to be in a particular position in order to work (for instance,
    //
    // "validation.standard.lastname.length.min=Your last name should have at least {2} characters"
    //
    // expects the actual useful argument to be in array index 2, and this is a resource bundle argument that potentially
    // could be got from either the data layer or the web layer, so it's best that we retain the original order of arguments
    // in the data layer to make these resource bundle entries reusable.  Therefore, we're best off just replacing the
    // MessageSourceResolvable argument with a blank entry.
    //
    private List<Object> getArgumentsFromBindingError(ObjectError e) {
        Object[] originalArguments = e.getArguments();
        return simpleMap(asList(originalArguments), arg -> arg instanceof MessageSourceResolvable ? "" : arg);
    }
}

