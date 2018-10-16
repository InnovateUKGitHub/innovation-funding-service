package org.innovateuk.ifs.registration.form;


/**
 * Form field model for registration of stakeholder users
 */
public class StakeholderRegistrationForm {
    private String firstName;
    private String lastName;
    private String password;

    public StakeholderRegistrationForm(String firstName, String lastName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

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
}
