package org.innovateuk.ifs.registration.resource;

import org.innovateuk.ifs.user.resource.UserResource;


/**
 * DTO for registering a Stakeholder user
 */
public class StakeholderRegistrationResource {
    private String firstName;
    private String lastName;
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserResource toUserResource() {
        UserResource userResource = new UserResource();
        userResource.setFirstName(this.getFirstName());
        userResource.setLastName(this.getLastName());
        userResource.setPassword(this.getPassword());
        return userResource;
    }
}
