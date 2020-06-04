package org.innovateuk.ifs.project.grants.form;

import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource.GrantsInviteRole;

public class GrantsSendInviteForm {

    private String firstName;
    private String lastName;
    private String email;
    private GrantsInviteRole role;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GrantsInviteRole getRole() {
        return role;
    }

    public void setRole(GrantsInviteRole role) {
        this.role = role;
    }
}
