package com.worth.ifs.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JsonStatusResponse {

    private static final Log log = LogFactory.getLog(JsonStatusResponse.class);

    private String message;

    @SuppressWarnings("unused")
    private JsonStatusResponse() {
        // for JSON marshalling
    }

    private JsonStatusResponse(String message) {
        this.message = message;
    }

    public static JsonStatusResponse ok() {
        return new JsonStatusResponse("OK");
    }

    public static JsonStatusResponse ok(String message) {
        return new JsonStatusResponse(message);
    }

    public static JsonStatusResponse badRequest(String message, HttpServletResponse response) {
        sendHttpResponseCode(response, HttpServletResponse.SC_BAD_REQUEST);
        return new JsonStatusResponse(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private static void sendHttpResponseCode(HttpServletResponse response, int httpServletResponseScCode) {
        try {
            response.sendError(httpServletResponseScCode);
        } catch (IOException e) {
            log.error("Error sending error response code to response", e);
        }
    }
}
