package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.invite.constant.InviteStatus;

public class ExternalInviteResource {
    private String name;
    private String organisationName;
    private String organisationId;
    private String email;
    private Long applicationId;
    private InviteStatus status;

    public ExternalInviteResource(String name, String organisationName, String organisationId, String email, Long applicationId, InviteStatus status) {
        this.name = name;
        this.organisationName = organisationName;
        this.organisationId = organisationId;
        this.email = email;
        this.applicationId = applicationId;
        this.status = status;
    }

    public ExternalInviteResource() {
    }

    public String getName() {
        return name;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public String getEmail() {
        return email;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public InviteStatus getStatus() {
        return status;
    }
}
