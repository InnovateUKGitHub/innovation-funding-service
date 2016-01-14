package com.worth.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents an invite for a Collaborator to join an Application
 */
public class InviteCollaboratorResource {

    private String recipientName;
    private String recipientEmail;

    /**
     * For JSON marshalling
     */
    InviteCollaboratorResource() {
    }

    public InviteCollaboratorResource(String recipientName, String recipientEmail) {
        this.recipientName = recipientName;
        this.recipientEmail = recipientEmail;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InviteCollaboratorResource that = (InviteCollaboratorResource) o;

        return new EqualsBuilder()
                .append(recipientName, that.recipientName)
                .append(recipientEmail, that.recipientEmail)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(recipientName)
                .append(recipientEmail)
                .toHashCode();
    }
}
