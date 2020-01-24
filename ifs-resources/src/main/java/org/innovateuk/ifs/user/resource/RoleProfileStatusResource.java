package org.innovateuk.ifs.user.resource;

public class RoleProfileStatusResource {

    private long userId;

    private RoleProfileState roleProfileState;

    private String rejectionReason;

    public RoleProfileStatusResource() {
    }

    public RoleProfileStatusResource(long userId, RoleProfileState roleProfileState, String rejectionReason) {
        this.userId = userId;
        this.roleProfileState = roleProfileState;
        this.rejectionReason = rejectionReason;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public RoleProfileState getRoleProfileState() {
        return roleProfileState;
    }

    public void setRoleProfileState(RoleProfileState roleProfileState) {
        this.roleProfileState = roleProfileState;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
