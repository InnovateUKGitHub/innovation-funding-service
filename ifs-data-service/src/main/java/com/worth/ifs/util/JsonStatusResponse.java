package com.worth.ifs.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class represents the response of a JSON call, and provides factory methods to create standard response.
 * Optionally it also sets an appropriate HTTP status code to a given HttpResponse
 *
 * Created by dwatson on 01/10/15.
 */
public class JsonStatusResponse {

    private static final Log log = LogFactory.getLog(JsonStatusResponse.class);

    private String message;

    @SuppressWarnings("unused")
    protected JsonStatusResponse() {
        // for JSON marshalling
    }

    protected JsonStatusResponse(String message) {
        this.message = message;
    }

    public static JsonStatusResponse ok() {
        return new JsonStatusResponse("OK");
    }

    public static JsonStatusResponse ok(String message) {
        return new JsonStatusResponse(message);
    }

    public static JsonStatusResponse badRequest(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, HttpServletResponse.SC_BAD_REQUEST);
    }

    public static JsonStatusResponse lengthRequired(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, HttpServletResponse.SC_LENGTH_REQUIRED);
    }

    public static JsonStatusResponse payloadTooLarge(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
    }

    private static JsonStatusResponse getJsonStatusResponse(String message, HttpServletResponse response, int scRequestEntityTooLarge) {
        sendHttpResponseCode(response, scRequestEntityTooLarge);
        return new JsonStatusResponse(message);
    }

    public static JsonStatusResponse internalServerError(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    public static JsonStatusResponse unsupportedMediaType(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
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
