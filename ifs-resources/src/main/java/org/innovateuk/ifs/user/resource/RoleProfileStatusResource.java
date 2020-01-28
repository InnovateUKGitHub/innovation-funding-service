package org.innovateuk.ifs.user.resource;

public class RoleProfileStatusResource {

    private Long userId;

    private RoleProfileState roleProfileState;

    private ProfileRole profileRole;

    private String description;

    public RoleProfileStatusResource() {
    }

    public RoleProfileStatusResource(long userId, RoleProfileState roleProfileState, ProfileRole profileRole) {
        this.userId = userId;
        this.roleProfileState = roleProfileState;
        this.profileRole = profileRole;
    }

    public RoleProfileStatusResource(long userId, RoleProfileState roleProfileState, ProfileRole profileRole, String description) {
        this.userId = userId;
        this.roleProfileState = roleProfileState;
        this.profileRole = profileRole;
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public RoleProfileState getRoleProfileState() {
        return roleProfileState;
    }

    public void setRoleProfileState(RoleProfileState roleProfileState) {
        this.roleProfileState = roleProfileState;
    }

    public ProfileRole getProfileRole() {
        return profileRole;
    }

    public void setProfileRole(ProfileRole profileRole) {
        this.profileRole = profileRole;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
