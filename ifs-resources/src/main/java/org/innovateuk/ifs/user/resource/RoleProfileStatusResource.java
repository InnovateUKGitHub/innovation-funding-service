package org.innovateuk.ifs.user.resource;

import java.time.ZonedDateTime;

public class RoleProfileStatusResource {

    private Long userId;

    private RoleProfileState roleProfileState;

    private ProfileRole profileRole;

    private String description;

    private Long createdBy;

    private ZonedDateTime createdOn;

    private Long modifiedBy;

    private ZonedDateTime modifiedOn;

    public RoleProfileStatusResource() {
    }

    public RoleProfileStatusResource(Long userId, RoleProfileState roleProfileState, ProfileRole profileRole, String description) {
        this.userId = userId;
        this.roleProfileState = roleProfileState;
        this.profileRole = profileRole;
        this.description = description;
    }

    public RoleProfileStatusResource(Long userId,
                                     RoleProfileState roleProfileState,
                                     ProfileRole profileRole,
                                     String description,
                                     Long createdBy,
                                     ZonedDateTime createdOn,
                                     Long modifiedBy,
                                     ZonedDateTime modifiedOn) {
        this.userId = userId;
        this.roleProfileState = roleProfileState;
        this.profileRole = profileRole;
        this.description = description;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.modifiedBy = modifiedBy;
        this.modifiedOn = modifiedOn;
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public ZonedDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(ZonedDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
}
