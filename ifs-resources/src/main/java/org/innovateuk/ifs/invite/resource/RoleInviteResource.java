package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.user.resource.RoleResource;

/**
 * Created by rav on 30/06/2017.
 */
public class RoleInviteResource extends InviteResource {
    private Long id;
    private String name;
    private String email;
    private RoleResource roleResource;

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

    public RoleResource getRoleResource() {
        return roleResource;
    }

    public void setRoleResource(RoleResource roleResource) {
        this.roleResource = roleResource;
    }
}
