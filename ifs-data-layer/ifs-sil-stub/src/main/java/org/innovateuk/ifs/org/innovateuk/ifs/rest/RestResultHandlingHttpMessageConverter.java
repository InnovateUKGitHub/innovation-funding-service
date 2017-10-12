package org.innovateuk.ifs.org.innovateuk.ifs.rest;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

// TODO qqRP This is a copy from the data-service-commons.
// TODO qqRP Should it live in ifs-commons, not necessarily as a component but as a class?
@Component
public class RestResultHandlingHttpMessageConverter extends MappingJackson2HttpMessageConverter {

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
