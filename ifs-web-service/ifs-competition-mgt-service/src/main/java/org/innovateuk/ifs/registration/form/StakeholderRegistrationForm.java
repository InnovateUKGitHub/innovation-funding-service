package org.innovateuk.ifs.registration.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class StakeholderRegistrationForm {
    private String firstName;
    private String lastName;
    private String password;

    public StakeholderRegistrationForm() { }

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

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//
//        if (o == null || getClass() != o.getClass()) return false;
//
//        InternalUserRegistrationForm that = (InternalUserRegistrationForm) o;
//
//        return new EqualsBuilder()
//                .append(firstName, that.firstName)
//                .append(lastName, that.lastName)
//                .append(password, that.password)
//                .isEquals();
//    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(firstName)
                .append(lastName)
                .append(password)
                .toHashCode();
    }
}
