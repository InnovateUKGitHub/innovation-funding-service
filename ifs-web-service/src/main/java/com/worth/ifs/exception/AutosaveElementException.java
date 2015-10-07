package com.worth.ifs.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.DateTimeException;
import java.util.Arrays;
import java.util.List;

/**
 * This object is used when a autosave is unsuccessful. We can then set the error
 * message to display to the user, or configure logging.
 */
public class AutosaveElementException extends RuntimeException {

    private interface ExceptionMessage {
        String getMessage(Throwable t);
    }

    private enum HandledExceptions implements ExceptionMessage {

        INVALID_DATE(DateTimeException.class) {

            @Override
            public String getMessage(Throwable t) {
                return "Please enter a valid date.";
            }
        };

        private List<Class<? extends Throwable>> exceptions;

        private HandledExceptions(Class<? extends Throwable> exceptions) {
            this.exceptions = Arrays.asList(exceptions);
        }

        private boolean handles(Throwable e) {
            return exceptions.contains(e.getClass());
        }

        static HandledExceptions getHandler(Throwable e) {
            for (HandledExceptions handler : HandledExceptions.values()) {
                if (handler.handles(e)) {
                    return handler;
                }
            }

            return null;
        }
    }

    private String errorMessage;
    private String inputIdentifier;
    private String value;
    private Long applicationId;

    public AutosaveElementException(String inputIdentifier, String value, Long applicationId, Throwable originalException) {
        super(originalException);

        errorMessage = resolveErrorMessage(originalException);
        this.inputIdentifier = inputIdentifier;
        this.value = value;
        this.applicationId = applicationId;
    }

    private String resolveErrorMessage(Throwable originalException) {

        HandledExceptions handler = HandledExceptions.getHandler(originalException);

        if (handler != null) {
            return handler.getMessage(originalException);
        }

        return "Please enter a valid value.";
    }

    ObjectNode createJsonResponse() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("success", "false");
        node.put("errorMessage", errorMessage);
        node.put("inputIdentifier", inputIdentifier);
        node.put("value", value);
        node.put("applicationId", applicationId);
        return node;
    }


}
