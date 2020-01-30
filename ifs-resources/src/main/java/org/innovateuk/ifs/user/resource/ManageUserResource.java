package org.innovateuk.ifs.user.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;

public class ManageUserResource {

    private long id;
    private String name;
    private String email;
    private List<Role> roles = new ArrayList<>();
    private ZonedDateTime createdOn;
    private String createdBy;
    private Set<RoleProfileStatusResource> roleProfileStatusResourceSet;

    public ManageUserResource() {
    }

    public ManageUserResource(long id,
                              String name,
                              String email,
                              List<Role> roles,
                              ZonedDateTime createdOn,
                              String createdBy,
                              Set<RoleProfileStatusResource> roleProfileStatusResourceSet) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.roleProfileStatusResourceSet = roleProfileStatusResourceSet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Set<RoleProfileStatusResource> getRoleProfileStatusResourceSet() {
        return roleProfileStatusResourceSet;
    }

    public void setRoleProfileStatusResourceSet(Set<RoleProfileStatusResource> roleProfileStatusResourceSet) {
        this.roleProfileStatusResourceSet = roleProfileStatusResourceSet;
    }

    @JsonIgnore
    public String getRoleDisplayNames() {
        if (roles.contains(IFS_ADMINISTRATOR)) {
            return IFS_ADMINISTRATOR.getDisplayName();
        }
        return roles.stream().map(this::getRoleDisplay).collect(joining(", "));
    }

    @JsonIgnore
    private String getRoleDisplay(Role role) {
        if (role.equals(Role.ASSESSOR)) {
            Optional<RoleProfileStatusResource> roleProfileStatusResource = this.roleProfileStatusResourceSet.stream()
                    .filter(i -> i.getProfileRole().equals(ProfileRole.ASSESSOR))
                    .findAny();

            if (roleProfileStatusResource.isPresent()
                    && !roleProfileStatusResource.get().getRoleProfileState().equals(RoleProfileState.ACTIVE)) {

                return ASSESSOR.displayName + " (" + roleProfileStatusResource.get().getRoleProfileState().getDescription() + ")";
            }
        }

        return role.displayName;
    }

}
