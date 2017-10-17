package org.innovateuk.ifs.user.resource;

public class UserOrganisationResource {
    private UserResource userResource;
    private Long organisationId;
    private String organisationName;

    public UserOrganisationResource(UserResource userResource, Long organisationId, String organisationName) {
        this.userResource = userResource;
        this.organisationId = organisationId;
        this.organisationName = organisationName;
    }

    public UserOrganisationResource() {
    }

    public UserResource getUserResource() {
        return userResource;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }
}
