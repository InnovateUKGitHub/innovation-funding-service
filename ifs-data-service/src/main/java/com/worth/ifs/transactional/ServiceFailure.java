package com.worth.ifs.transactional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

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

    public ServiceFailure(List<GlobalError> errors) {
        this.errors = errors;
    }

    public static ServiceFailure error(List<String> messages) {
        return new ServiceFailure(messages.stream().map(GlobalError::new).collect(toList()));
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
        return error(asList(messages).stream().map(Enum::name).collect(toList()));
    }

    public boolean is(String... messages) {
        List<String> containedErrors = errors.stream().map(GlobalError::getMessage).collect(toList());
        List<String> messagesList = asList(messages);
        return containedErrors.containsAll(messagesList) && messagesList.containsAll(containedErrors);
    }

    public boolean is(Enum<?>... messages) {
        List<String> var = asList(messages).stream().map(Enum::name).collect(toList());
        return is(var.toArray(new String[var.size()]));
    }
}
