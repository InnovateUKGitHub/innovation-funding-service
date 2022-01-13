package org.innovateuk.ifs.invite.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.user.resource.Role;

import static org.innovateuk.ifs.user.resource.Role.externalRolesToInvite;

public class RoleInviteResource extends InviteResource {
    private Long id;
    private String name;
    private String email;
    private String hash;
    private Role role;
    private String organisation;

    public RoleInviteResource() {
    }

    public RoleInviteResource(Long id, String name, String email, String hash, String organisation) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hash = hash;
        this.organisation = organisation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    @JsonIgnore
    public String getRoleDisplayName() {
        return role.getDisplayName();
    }

    @JsonIgnore
    public boolean isExternalInvite() {
        return externalRolesToInvite().contains(role);
    }
}
