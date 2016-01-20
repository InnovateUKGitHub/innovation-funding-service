package com.worth.ifs.authentication.resource;

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
}
