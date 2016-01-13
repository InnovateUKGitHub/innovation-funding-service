package com.worth.ifs.application.resource;

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
}
