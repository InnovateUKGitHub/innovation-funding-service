package com.worth.ifs.rest;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorEnvelope;
import com.worth.ifs.commons.rest.RestResult;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 *
 */
@Component
public class CustomRestResultHandlingHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        RestResult<?> restResult = null;

        if (object != null && object instanceof RestResult && outputMessage instanceof ServerHttpResponse) {
            restResult = (RestResult<?>) object;
        }

        if (restResult != null) {

            ServerHttpResponse serverHttpResponse = (ServerHttpResponse) outputMessage;
            serverHttpResponse.setStatusCode(restResult.getStatusCode());

            if (restResult.isLeft()) {
                List<Error> errors = restResult.getLeft().getErrors();
                super.writeInternal(new RestErrorEnvelope(errors), type, outputMessage);
            } else {

                if (!restResult.isBodiless()) {
                    super.writeInternal(restResult.getRight().getResult(), type, outputMessage);
                }
            }

        } else {
            super.writeInternal(object, type, outputMessage);
        }
    }
}
