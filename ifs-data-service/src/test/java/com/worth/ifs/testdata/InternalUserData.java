package com.worth.ifs.testdata;

import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;

/**
 * TODO DW - document this class
 */
public class InternalUserData {

    private UserResource user;
    private UserRoleType role;
    private String emailAddress;

    public UserResource getUser() {
        return user;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }

    public UserRoleType getRole() {
        return role;
    }

    public void setRole(UserRoleType role) {
        this.role = role;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
