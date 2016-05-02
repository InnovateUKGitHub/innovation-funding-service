package com.worth.ifs.user.resource;

import java.util.ArrayList;
import java.util.List;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Response;

public class ProcessRoleResource {
    private Long id;
    private Long user;
    private Long application;
    private Long role;
    private String roleName;
    private Long organisation;
    private List<Response> responses = new ArrayList<>();

    public ProcessRoleResource(){
    	// no-arg constructor
    }

    public ProcessRoleResource(Long id, UserResource user, Application application, RoleResource role, OrganisationResource organisation) {
        this.id = id;
        this.user = user.getId();
        this.application = application.getId();
        this.role = role.getId();
        this.roleName = role.getName();
        this.organisation = organisation.getId();
    }

    public Long getId(){return id;}

    public Long getUser() {
        return user;
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

    public List<Response> getResponses() {
        return this.responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
