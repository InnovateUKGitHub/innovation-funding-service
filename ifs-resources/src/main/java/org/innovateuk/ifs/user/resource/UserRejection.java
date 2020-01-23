package org.innovateuk.ifs.user.resource;

public class UserRejection {

    private UserStatus userStatus;

    private String reason;

    public UserRejection() {
    }

    public UserRejection(UserStatus userStatus, String reason) {
        this.userStatus = userStatus;
        this.reason = reason;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
