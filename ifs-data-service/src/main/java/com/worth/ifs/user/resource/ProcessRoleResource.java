package com.worth.ifs.user.resource;

import com.worth.ifs.application.resource.ApplicationResource;

public class ProcessRoleResource {
    private Long id;
    private Long user;
    private String userName;
    private Long application;
    private Long role;
    private String roleName;
    private Long organisation;

    public ProcessRoleResource(){
    	// no-arg constructor
    }

    public ProcessRoleResource(Long id, UserResource user, ApplicationResource application, RoleResource role, OrganisationResource organisation) {
        this.id = id;
        this.user = user.getId();
        this.userName = user.getName();
        this.application = application.getId();
        this.role = role.getId();
        this.roleName = role.getName();
        this.organisation = organisation.getId();
    }

    public Long getId(){return id;}

    public Long getUser() {
        return user;
    }

    public String getUserName() {
        return userName;
    }

    public Long getRole() {
        return role;
    }

    public String getRoleName() {
        return roleName;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public Long getApplication() {
        return this.application;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setApplication(Long application) {
        this.application = application;
    }

    public void setRole(Long role) {
        this.role = role;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
