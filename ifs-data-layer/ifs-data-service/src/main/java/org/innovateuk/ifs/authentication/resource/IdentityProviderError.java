package org.innovateuk.ifs.authentication.resource;

import java.util.List;

/**
 * Represents an error being returned from a call to the Identity Provider REST service
 */
public class IdentityProviderError {

    private String key;
    private List<String> arguments;

    /**
     * For JSON marshalling
     */
    public IdentityProviderError() {
    	// no-arg constructor
    }

    public IdentityProviderError(String key, List<String> arguments) {
        this.key = key;
        this.arguments = arguments;
    }

    public String getKey() {
        return key;
    }

    public List<String> getArguments() {
        return arguments;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentityProviderError that = (IdentityProviderError) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (arguments != null ? !arguments.equals(that.arguments) : that.arguments != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        return result;
    }
}
