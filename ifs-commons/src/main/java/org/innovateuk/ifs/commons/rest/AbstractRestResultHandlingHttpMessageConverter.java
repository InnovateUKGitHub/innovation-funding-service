package org.innovateuk.ifs.commons.rest;

import org.innovateuk.ifs.commons.error.Error;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * A custom extension of the default MappingJackson2HttpMessageConverter, that additionally knows how to treat RestResults
 * as a special case.  Specifically, this class intercepts Controller methods that are returning RestResults and,
 * before passing them down to the default MappingJackson2HttpMessageConverter for converting to JSON, unpacks them, determines
 * whether or not they represent a success or a failure case, applies the success or failure HTTP status code, and, in the case of
 * a success, allows the "success resource" to be converted to JSON and, in the event of a failure case coverts the failure to
 * a standard RestErrorResponse.
 *
 * In this way, we have a consistent error-handling mechanism over REST for Controller methods that are returning RestResults.
 *
 * Note that this does not have the {@link Component} annotation as doing so would force dependent projects to provide
 * the dependencies of this class whether or not it is needed. Instead this class should be subclasses where it is
 * required and the annotation added to that.
 */
public class AbstractRestResultHandlingHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        RestResult<?> restResult = getRestResultIfAvailable(object);

        if (restResult != null) {
            handleRestResultSuccessOrFailure(type, outputMessage, restResult);
        } else {
            super.writeInternal(object, type, outputMessage);
        }
    }

    private void handleRestResultSuccessOrFailure(Type type, HttpOutputMessage outputMessage, RestResult<?> restResult) throws IOException {

        ServerHttpResponse serverHttpResponse = (ServerHttpResponse) outputMessage;
        serverHttpResponse.setStatusCode(restResult.getStatusCode());

        if (restResult.isFailure()) {
            List<Error> errors = restResult.getFailure().getErrors();
            super.writeInternal(new RestErrorResponse(errors), type, outputMessage);
        } else {

            if (restResult.getSuccessObject() != null) {
                super.writeInternal(restResult.getSuccessObject(), type, outputMessage);
            }
        }
    }

    private RestResult<?> getRestResultIfAvailable(Object object) {
        RestResult<?> restResult = null;

        if (object != null && object instanceof RestResult) {
            restResult = (RestResult<?>) object;
        }
        return restResult;
    }
}
