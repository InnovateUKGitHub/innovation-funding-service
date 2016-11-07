package com.worth.ifs.testdata.builders.data;

import com.worth.ifs.user.resource.UserRoleType;

/**
 * TODO DW - document this class
 */
public class InternalUserData extends BaseUserData {

    private UserRoleType role;
    private String emailAddress;

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
