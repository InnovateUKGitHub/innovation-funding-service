package org.innovateuk.ifs.user.resource;

import org.innovateuk.ifs.application.resource.ApplicationResource;

public class ProcessRoleResource {
    private Long id;
    private Long user;
    private String userName;
    private Long applicationId;
    private Role role;
    private String roleName;
    private Long organisationId;

    public ProcessRoleResource(){
    	// no-arg constructor
    }

    public ProcessRoleResource(Long id, UserResource user, ApplicationResource application, Role role, OrganisationResource organisation) {
        this.id = id;
        this.user = user.getId();
        this.userName = user.getName();
        this.applicationId = application.getId();
        this.role = role;
        this.roleName = role.getName();
        this.organisationId = organisation.getId();
    }

    public Long getId(){return id;}

    public Long getUser() {
        return user;
    }

    public String getUserName() {
        return userName;
    }

    public Role getRole() {
        return role;
    }

    public String getRoleName() {
        return roleName;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public Long getApplicationId() {
        return this.applicationId;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
