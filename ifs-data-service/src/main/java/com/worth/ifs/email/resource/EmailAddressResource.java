package com.worth.ifs.email.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 */
public class EmailAddressResource {

    private String emailAddress;
    private String name;

    public EmailAddressResource(String emailAddress, String name) {
        this.emailAddress = emailAddress;
        this.name = name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EmailAddressResource that = (EmailAddressResource) o;

        return new EqualsBuilder()
                .append(emailAddress, that.emailAddress)
                .append(name, that.name)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(emailAddress)
                .append(name)
                .toHashCode();
    }
}
