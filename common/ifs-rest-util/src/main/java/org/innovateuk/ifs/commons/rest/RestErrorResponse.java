package org.innovateuk.ifs.commons.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.error.ErrorTemplate;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.util.MapFunctions.getSortedGroupingCounts;

/**
 * A standard error transport mechanism for any errors that we wish to report over REST.
 * Multiple Errors can be combined here and reported back to the client.  The overall HTTP status code for the
 * multiple errors is derived from the status codes on the individual Errors.
 */
@Slf4j
public class RestErrorResponse {

    private List<Error> errors;

    /**
     * For JSON marshalling
     */
    public RestErrorResponse() {
    	// no-arg constructor
    }

    public RestErrorResponse(Error error) {
        this(singletonList(error));
    }

    public RestErrorResponse(List<Error> errors) {
        this.errors = errors;
    }

    public RestErrorResponse(ValidationMessages validationMessages) {
        this.errors = validationMessages.getErrors();
    }

    public List<Error> getErrors() {
        return errors;
    }

    @JsonIgnore
    public HttpStatus getStatusCode() {

        LinkedHashMap<HttpStatus, Integer> entries = getHttpStatusCounts();
        return entries.entrySet().iterator().next().getKey();
    }

    private LinkedHashMap<HttpStatus, Integer> getHttpStatusCounts() {
        return getSortedGroupingCounts(errors, Error::getStatusCode);
    }

    public boolean is(ErrorTemplate errorTemplate, List<Object> arguments) {

        List<Error> expectedErrors = singletonList(new Error(errorTemplate, arguments));
        return errorListsMatch(expectedErrors);
    }

    public boolean is(Error error) {

        List<Error> expectedErrors = singletonList(error);
        return errorListsMatch(expectedErrors);
    }

    private boolean errorListsMatch(List<Error> expectedErrors) {

        if (expectedErrors.size() != errors.size()) {
            log.warn("Error lists don't match by size - expected " + expectedErrors + " but got " + errors);
            return false;
        }

        return errors.containsAll(expectedErrors);
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
        RestErrorResponse rhs = (RestErrorResponse) obj;
        return new EqualsBuilder()
                .append(this.errors, rhs.errors)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(errors)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("errors", errors)
                .toString();
    }
}
