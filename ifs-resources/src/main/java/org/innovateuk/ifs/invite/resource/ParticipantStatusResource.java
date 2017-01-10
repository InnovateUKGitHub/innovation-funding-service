package org.innovateuk.ifs.invite.resource;

/**
 * DTO for {@link org.innovateuk.ifs.invite.domain.ParticipantStatus}es.
 */
public enum ParticipantStatusResource {
    PENDING("Awaiting response"),
    ACCEPTED("Invite accepted"),
    REJECTED("Invite declined");

    private String displayName;

    ParticipantStatusResource(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

