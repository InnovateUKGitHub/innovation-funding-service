package com.worth.ifs.transactional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;

/**
 * This class represents a failure encountered during a service call and can additionally contain 0 or more error
 * messages within it.
 *
 * Code that returns
 *
 * Created by dwatson on 06/10/15.
 */
public class ServiceFailure {

    public static class GlobalError {

        private final String message;

        private GlobalError(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            GlobalError rhs = (GlobalError) obj;
            return new EqualsBuilder()
                .append(this.message, rhs.message)
                .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                .append(message)
                .toHashCode();
        }
    }

    private List<GlobalError> errors;
    private Throwable cause;

    private ServiceFailure(List<GlobalError> errors, Throwable e) {
        this.errors = errors;
        this.cause = e;
    }

    public static ServiceFailure error(List<String> messages) {
        return new ServiceFailure(simpleMap(messages, GlobalError::new), null);
    }

    public static ServiceFailure error(List<String> messages, Throwable e) {
        return new ServiceFailure(simpleMap(messages, GlobalError::new), e);
    }

    public static ServiceFailure error(String... messages) {
        return error(asList(messages));
    }

    public static ServiceFailure error(String message) {
        return error(new String[] { message });
    }

    public static ServiceFailure error(Enum<?> message) {
        return error(message.name());
    }

    public static ServiceFailure error(Enum<?>... messages) {
        return error(simpleMap(asList(messages), Enum::name));
    }

    public static ServiceFailure error(Throwable e, String... messages) {
        return error(asList(messages), e);
    }

    public static ServiceFailure error(String message, Throwable e) {
        return error(e, new String[] { message });
    }

    public static ServiceFailure error(Enum<?> message, Throwable e) {
        return error(message.name(), e);
    }

    public static ServiceFailure error(Throwable e, Enum<?>... messages) {
        return error(simpleMap(asList(messages), Enum::name), e);
    }

    public boolean is(String... messages) {
        List<String> containedErrors = simpleMap(errors, GlobalError::getMessage);
        List<String> messagesList = asList(messages);
        return containedErrors.containsAll(messagesList) && messagesList.containsAll(containedErrors);
    }

    public boolean is(Enum<?>... messages) {
        List<String> var = simpleMap(asList(messages), Enum::name);
        return is(var.toArray(new String[var.size()]));
    }

    public boolean contains(Enum<?>... messages) {
        List<String> messagesToCheck = simpleMap(asList(messages), Enum::name);
        return contains(messagesToCheck.toArray(new String[messagesToCheck.size()]));
    }

    public boolean contains(String... messages) {
        List<String> containedErrors = simpleMap(errors, GlobalError::getMessage);
        return containedErrors.containsAll(asList(messages));
    }

    public List<String> getErrors() {
        return simpleMap(errors, GlobalError::getMessage);
    }

    public Throwable getCause() {
        return cause;
    }
}
