package com.worth.ifs.user.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.resource.ResourceWithEmbeddeds;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.Role;
import com.worth.ifs.user.domain.User;
import org.springframework.hateoas.core.Relation;

@Relation(value="processRole", collectionRelation="processRoles")
public class ProcessRoleResource extends ResourceWithEmbeddeds {

    private Long id;
    private User user;
    private Application application;
    private Role role;
    private Organisation organisation;

    @JsonCreator
    public ProcessRoleResource(@JsonProperty("id") Long id,
                               @JsonProperty("user") User user,
                               @JsonProperty("application") Application application,
                               @JsonProperty("role") Role role,
                               @JsonProperty("organisation") Organisation organisation
    ){
        super();
        this.id = id;
        this.user = user;
        this.application = application;
        this.role = role;
        this.organisation = organisation;
    }

    public ProcessRole toProcessRole() {
        return new ProcessRole(this.id, this.user, this.application, this.role, this.organisation);
    }

    public User getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }

    public Organisation getOrganisation() {
        return organisation;
    }
}
