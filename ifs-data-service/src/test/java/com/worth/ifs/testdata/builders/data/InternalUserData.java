package com.worth.ifs.testdata.builders.data;

import com.worth.ifs.user.resource.UserRoleType;

/**
 * Running data context for generating Internal Users (e.g. comp admins)
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
