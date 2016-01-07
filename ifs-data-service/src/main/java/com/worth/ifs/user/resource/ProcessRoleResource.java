package com.worth.ifs.user.resource;

import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.domain.Response;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.springframework.hateoas.core.Relation;

import java.util.ArrayList;
import java.util.List;

@Relation(value="processRole", collectionRelation="processRoles")
public class ProcessRoleResource {
    private Long id;
    private Long user;
    private Long application;
    private Long role;
    private Long organisation;
    private List<Response> responses = new ArrayList<>();

    public ProcessRoleResource(){}

    public ProcessRoleResource(Long id, User user, Application application, Role role, Organisation organisation
    ) {
        this.id = id;
        this.user = user.getId();
        this.application = application.getId();
        this.role = role.getId();
        this.organisation = organisation.getId();
    }

    public Long getId(){return id;}

    public Long getUser() {
        return user;
    }

    public Long getRole() {
        return role;
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

    public void setOrganisation(Long organisation) {
        this.organisation = organisation;
    }

    public List<Response> getResponses() {
        return this.responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }
}
