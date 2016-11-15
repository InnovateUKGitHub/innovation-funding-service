package com.worth.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents an invite for a Collaborator to join an com.worth.ifs.Application
 */
public class InviteCollaboratorResource {

    private String recipientName;
    private String recipientEmail;
    private String recipientInviteLink;

    /**
     * For JSON marshalling
     */
    InviteCollaboratorResource() {
    }

    public InviteCollaboratorResource(String recipientName, String recipientEmail, String recipientInviteLink) {
        this.recipientName = recipientName;
        this.recipientEmail = recipientEmail;
        this.recipientInviteLink = recipientInviteLink;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getRecipientInviteLink() {
        return recipientInviteLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InviteCollaboratorResource that = (InviteCollaboratorResource) o;

        return new EqualsBuilder()
                .append(recipientName, that.recipientName)
                .append(recipientEmail, that.recipientEmail)
                .append(recipientInviteLink, that.recipientInviteLink)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(recipientName)
                .append(recipientEmail)
                .append(recipientInviteLink)
                .toHashCode();
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public void setRecipientInviteLink(String recipientInviteLink) {
        this.recipientInviteLink = recipientInviteLink;
    }
}
