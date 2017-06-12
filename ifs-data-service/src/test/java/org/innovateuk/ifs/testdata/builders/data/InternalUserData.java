package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.user.resource.UserRoleType;

import java.util.List;

/**
 * Running data context for generating Internal Users (e.g. comp admins)
 */
public class InternalUserData extends BaseUserData {

    private List<UserRoleType> roles;

    private String emailAddress;

/*    public UserRoleType getRole() {
        return role;
    }

    public void setRole(UserRoleType role) {
        this.role = role;
    }*/

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public List<UserRoleType> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRoleType> roles) {
        this.roles = roles;
    }
}
