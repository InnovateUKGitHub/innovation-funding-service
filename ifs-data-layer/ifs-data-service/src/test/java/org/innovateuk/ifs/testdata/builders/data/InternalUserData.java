package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.user.resource.Role;

import java.util.List;

/**
 * Running data context for generating Internal Users (e.g. comp admins)
 */
public class InternalUserData extends BaseUserData {

    private List<Role> roles;

    private String emailAddress;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
