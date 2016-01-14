package com.worth.ifs.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.*;

/**
 * This class represents the response of a JSON call, and provides factory methods to create standard response.
 * Optionally it also sets an appropriate HTTP status code to a given HttpResponse
 *
 * Created by dwatson on 01/10/15.
 */
public class JsonStatusResponse {

    private static final Log log = LogFactory.getLog(JsonStatusResponse.class);

    private String message;

    @JsonIgnore
    private HttpStatus status;

    @SuppressWarnings("unused")
    protected JsonStatusResponse() {
        // for JSON marshalling
    }

    protected JsonStatusResponse(String message, int statusCode) {
        this(message, HttpStatus.valueOf(statusCode));
    }

    protected JsonStatusResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    protected JsonStatusResponse(String message, HttpStatus status, HttpServletResponse response) {
        this.message = message;
        this.status = status;
        sendHttpResponseCode(response, status.value());
    }

    public static JsonStatusResponse ok() {
        return new JsonStatusResponse("OK", SC_OK);
    }

    public static JsonStatusResponse ok(String message) {
        return new JsonStatusResponse(message, SC_OK);
    }

    public static JsonStatusResponse accepted(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, SC_ACCEPTED);
    }

    public static JsonStatusResponse noContent(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, SC_NO_CONTENT);
    }

    public static JsonStatusResponse badRequest(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, SC_BAD_REQUEST);
    }

    public static JsonStatusResponse conflict(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, SC_CONFLICT);
    }

    public static JsonStatusResponse lengthRequired(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, SC_LENGTH_REQUIRED);
    }

    public static JsonStatusResponse payloadTooLarge(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, SC_REQUEST_ENTITY_TOO_LARGE);
    }

    public static JsonStatusResponse internalServerError(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, SC_INTERNAL_SERVER_ERROR);
    }

    public static JsonStatusResponse unsupportedMediaType(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, SC_UNSUPPORTED_MEDIA_TYPE);
    }

    public static JsonStatusResponse notFound(String message, HttpServletResponse response) {
        return getJsonStatusResponse(message, response, SC_NOT_FOUND);
    }

    private static JsonStatusResponse getJsonStatusResponse(String message, HttpServletResponse response, int statusCode) {
        return new JsonStatusResponse(message, HttpStatus.valueOf(statusCode), response);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    private static void sendHttpResponseCode(HttpServletResponse response, int httpServletResponseScCode) {
        try {
            response.sendError(httpServletResponseScCode);
        } catch (IOException e) {
            log.error("Error sending error response code to response", e);
        }
    }
}
