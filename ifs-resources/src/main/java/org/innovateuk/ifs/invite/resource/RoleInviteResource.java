package org.innovateuk.ifs.invite.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.user.resource.UserRoleType;

/**
 * Created by rav on 30/06/2017.
 */
public class RoleInviteResource extends InviteResource {
    private Long id;
    private String name;
    private String email;
    private Long roleId;
    private String roleName;
    private String hash;

    public RoleInviteResource() {
    }

    public RoleInviteResource(Long id, String name, String email, Long roleId, String roleName, String hash) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roleId = roleId;
        this.roleName = roleName;
        this.hash = hash;
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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @JsonIgnore
    public String getRoleDisplayName(){
        return UserRoleType.fromName(roleName).getDisplayName();
    }
}
