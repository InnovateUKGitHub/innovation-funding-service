package org.innovateuk.ifs.starters.feign.cfg;

import com.google.common.io.ByteStreams;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Map feign exceptions back to the standard Spring ResponseStatusException
 */
@Slf4j
public class IfsErrorDecoder implements ErrorDecoder {

    private static final String MESSAGE_KEY = "message";

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "unknown";
        try {
            JSONObject jo = new JSONObject(new String(ByteStreams.toByteArray(response.body().asInputStream()), StandardCharsets.UTF_8));
            if (jo.has(MESSAGE_KEY)) {
                message = "" + jo.get(MESSAGE_KEY);
            }
        } catch (IOException e) {
            log.warn("Unable to extract underlying reason - either set server.error.include-message: always or there was no message to extract", e);
        }
        return new ResponseStatusException(HttpStatus.valueOf(response.status()), message);
    }
}
