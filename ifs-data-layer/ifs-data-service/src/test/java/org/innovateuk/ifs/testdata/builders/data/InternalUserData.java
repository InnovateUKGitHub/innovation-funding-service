package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.user.resource.Role;

/**
 * Running data context for generating Internal Users (e.g. comp admins)
 */
public class InternalUserData extends BaseUserData {

    private Role role;

    private String emailAddress;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
