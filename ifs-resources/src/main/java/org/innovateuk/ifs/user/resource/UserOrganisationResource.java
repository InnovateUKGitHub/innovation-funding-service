package org.innovateuk.ifs.user.resource;

public class UserOrganisationResource {
    private String name;
    private String organisationName;
    private Long organisationId;
    private String email;
    private UserStatus status;


    public UserOrganisationResource() {}

    public UserOrganisationResource(String name, String organisationName, Long organisationId, String email, UserStatus status) {
        this.name = name;
        this.organisationName = organisationName;
        this.organisationId = organisationId;
        this.email = email;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public String getEmail() {
        return email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
