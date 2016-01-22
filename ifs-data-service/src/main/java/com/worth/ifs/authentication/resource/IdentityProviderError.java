package com.worth.ifs.authentication.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Represents an error being returned from a call to the Identity Provider REST service
 */
public class IdentityProviderError {

    private String messageKey;
    private List<String> arguments;

    /**
     * For JSON marshalling
     */
    public IdentityProviderError() {
    }

    public IdentityProviderError(String messageKey, List<String> arguments) {
        this.messageKey = messageKey;
        this.arguments = arguments;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public List<String> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        IdentityProviderError that = (IdentityProviderError) o;

        return new EqualsBuilder()
                .append(messageKey, that.messageKey)
                .append(arguments, that.arguments)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(messageKey)
                .append(arguments)
                .toHashCode();
    }
}
